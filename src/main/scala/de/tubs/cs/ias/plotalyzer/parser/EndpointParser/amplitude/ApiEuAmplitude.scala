package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.amplitude

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.{BODY, PIILocation}
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsArray, JsObject, JsString, JsonParser}
import wvlet.log.LogSupport

import java.net.URLDecoder

class ApiEuAmplitude(override val request: Request)
    extends PIIParser
    with LogSupport {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    var query = super.convertQuery(body)
    if (!query.fields.contains("e")) { // this is android
      query = JsonParser(body).asJsObject
      query = JsObject(
        query.fields ++ Map("e" -> query.fields("events"),
                            "events" -> JsString("look at e"))
      )
    } else { // this is iOS
      JsonParser(
        URLDecoder
          .decode(query.fields("e").asInstanceOf[JsString].value)) match {
        case array: JsArray =>
          query = JsObject(
            query.fields ++ Map("e" -> array)
          )
        case _ =>
          error("unexpect e value")
          JsObject()
      }
    }
    query
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    bodyValues.fields("e").asInstanceOf[JsArray].elements.foreach {
      case element: JsObject =>
        addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "carrier")(element, BODY)
        addPii(PiiTypes.COUNTRY, "country")(element, BODY)
        addPii(PiiTypes.ID_UUID, "device_id")(element, BODY)
        addPii(PiiTypes.DEVICE_MAKER, "device_manufacturer")(element, BODY)
        addPii(PiiTypes.DEVICE_MODEL, "device_model")(element, BODY)
        addPii(PiiTypes.DEVICE_LANGUAGE, "language")(element, BODY)
        addPii(PiiTypes.DEVICE_OS_TYPE, "os_name")(element, BODY)
        addPii(PiiTypes.DEVICE_OS_VERSION, "os_version")(element, BODY)
        addPii(PiiTypes.DEVICE_OS_TYPE, "platform")
        addPii(PiiTypes.ID_OTHER, "session_id")
        addPii(PiiTypes.ID_UUID, "uuid")
        addPii(PiiTypes.ID_UUID, "insert_id")
    }
  }
}
