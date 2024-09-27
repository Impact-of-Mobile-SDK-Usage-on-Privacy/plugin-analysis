package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.firebase

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.halcony.plotalyzer.utility.json.DeepMerge
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.util.ProtobufToJson
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsArray, JsObject}

class FirebaseAppMeasurement(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    try {
      Option(request.contentRaw) match {
        case Some(value) =>
          val protoBufJson = ProtobufToJson.createJsonObject(
            com.google.protobuf.UnknownFieldSet.parseFrom(value))
          val messages = protoBufJson.fields.get("1") match {
            case Some(messages: JsArray) =>
              messages.elements.map(_.asJsObject).toList
            case Some(messages: JsObject) => List(messages)
            case _                        => List()
          }
          val ret = DeepMerge.arrayReduce(DeepMerge.merge(messages: _*))
          ret
        case None => JsObject()
      }
    } catch {
      case _: Throwable => JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.DEVICE_OS_TYPE, "8")
    addPii(PiiTypes.DEVICE_OS_VERSION, "9")
    addPii(PiiTypes.DEVICE_MODEL, "10")
    addPii(PiiTypes.ID_UUID, "21")
    addPii(PiiTypes.ID_UUID, "27")
    addPii(PiiTypes.ID_OTHER, "25")
    addPii(PiiTypes.APP_ID, "14")
  }
}
