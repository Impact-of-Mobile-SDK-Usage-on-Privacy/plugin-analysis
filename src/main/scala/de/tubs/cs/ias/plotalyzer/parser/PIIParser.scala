package de.tubs.cs.ias.plotalyzer.parser

import de.halcony.plotalyzer.database.entities.trafficcollection.{
  Header,
  Request
}
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.{
  BODY,
  HEADER,
  PIILocation,
  QUERY
}
import de.tubs.cs.ias.plotalyzer.parser.pii.{PII, PiiTypes}
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes.PiiType
import spray.json.{JsNull, JsNumber, JsObject, JsString, JsValue}
import wvlet.log.LogSupport

import scala.collection.mutable.ListBuffer

object PIILocations extends Enumeration {
  type PIILocation = Value
  val BODY, QUERY, HEADER = Value
}

abstract class PIIParser extends LogSupport {

  protected val request: Request
  private val pii: ListBuffer[PII] = ListBuffer()

  parsePII()

  protected def addPii(ptype: PiiType, path: String)(implicit values: JsValue,
                                                     loc: PIILocation): Unit = {
    var current: Option[JsValue] = Some(values)
    path.split('.').foreach { pathElement =>
      val lookingFor = pathElement.replace('#', '.')
      if (current.nonEmpty && current.get
            .isInstanceOf[JsObject] && current.get.asJsObject.fields.contains(
            lookingFor)) {
        current = Some(current.get.asJsObject.fields(lookingFor))
      } else {
        current = None
      }
    }

    current match {
      case Some(value) if value.isInstanceOf[JsString] =>
        pii.addOne(PII(ptype, value.asInstanceOf[JsString].value, request))
      case Some(value) if value.isInstanceOf[JsNumber] =>
        pii.addOne(
          PII(ptype, value.asInstanceOf[JsNumber].value.toString(), request))
      case Some(value) =>
        pii.addOne(PII(ptype, value.toString(), request))
      case None =>
        pii.addOne(PII(ptype, s"$path#MISSING@${loc.toString}", request))
    }
  }

  protected def convertQuery(query: String): JsObject = {
    if (query != null && query.nonEmpty) {
      JsObject(
        query
          .split('&')
          .map { keyValue =>
            keyValue.split('=').toList match {
              case Nil =>
                warn(s"bad key value pair $keyValue")
                "" -> JsNull
              case x :: Nil =>
                x -> JsNull
              case key :: value :: Nil => key -> JsString(value)
            }
          }
          .toMap)
    } else {
      JsObject()
    }
  }

  protected def convertBody(body: String, binary: Array[Byte]): JsObject

  protected def convertHeader(headers: List[Header]): JsObject = {
    JsObject(headers.map { header =>
      header.name -> JsString(header.values)
    }.toMap)
  }

  protected def parseQuery(implicit queryValues: JsObject,
                           PIILocation: PIILocation = QUERY): Unit
  protected def parseBody(implicit bodyValues: JsObject,
                          PIILocation: PIILocation = BODY): Unit
  protected def parseHeader(implicit headerValues: JsObject,
                            PIILocation: PIILocation = HEADER): Unit = {
    addPii(PiiTypes.USER_AGEND, "user-agent")
    addPii(PiiTypes.USER_AGEND, "User-Agent")
    addPii(PiiTypes.DEVICE_LANGUAGE, "accept-language")
    addPii(PiiTypes.DEVICE_LANGUAGE, "Accept-Language")
  }

  protected def parsePII(): Unit = {
    var query: JsObject = JsObject()
    var body: JsObject = JsObject()
    var header: JsObject = JsObject()
    if (request.getPathWithQuery.contains("?")) {
      query = request.getPathWithQuery.split('?').toList match {
        case Nil               => convertQuery("")
        case _ :: Nil          => convertQuery("")
        case _ :: query :: Nil => convertQuery(query)
        case _ :: query        => convertQuery(query.mkString("?"))
      }
    } else {
      query = convertQuery("")
    }
    parseQuery(query)
    body = convertBody(request.content, request.contentRaw)
    parseBody(body)
    header = convertHeader(request.headers)
    parseHeader(header)
  }

  def getPii: List[PII] = pii.toList

}
