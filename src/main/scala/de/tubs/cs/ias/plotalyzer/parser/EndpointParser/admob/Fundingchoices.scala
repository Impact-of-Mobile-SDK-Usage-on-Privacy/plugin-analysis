package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.admob

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsArray, JsObject, JsString, JsonParser}

class Fundingchoices(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body != null && body.nonEmpty) {
      try {
        JsonParser(body).asJsObject()
      } catch {
        case _: Throwable =>
          try {
            JsonParser(body) match {
              case array: JsArray =>
                array.elements.apply(16) match {
                  case array: JsArray =>
                    array.elements(3) match {
                      case array: JsArray =>
                        array.elements(1) match {
                          case array: JsArray =>
                            JsObject(
                              "unknown-uuid" -> JsString(
                                array.elements(0).asInstanceOf[JsString].value),
                              "IAB-consent-string" -> JsString(
                                array.elements(1).asInstanceOf[JsString].value)
                            )
                          case _ => JsObject()
                        }
                      case _ => JsObject()
                    }
                  case _ => JsObject()
                }
              case _ => JsObject()
            }
          } catch {
            case _: Throwable => JsObject()
          }
      }
    } else {
      JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.APP_ID, "appid")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_GLOBAL_ADID, "adid")
    addPii(PiiTypes.IAB_STRING, "IAB-consent-string")
    addPii(PiiTypes.ID_UUID, "unknown-uuid")

    addPii(PiiTypes.APP_ID, "app_info.package_name")
    addPii(PiiTypes.APP_VERSION, "app_info.version")

    addPii(PiiTypes.DEVICE_IS_SIMULATOR, "device_info.is_simulator")
    addPii(PiiTypes.DEVICE_MODEL, "device_info.model")
    addPii(PiiTypes.DEVICE_OS_TYPE, "device_info.os_type")
    addPii(PiiTypes.DEVICE_OS_VERSION, "device_info.version")

    addPii(PiiTypes.DEVICE_LANGUAGE, "language_code")

    addPii(PiiTypes.ID_GLOBAL_ADID, "rdid")

    addPii(PiiTypes.DEVICE_SCREEN_DENSITY, "screen_info.density")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "screen_info.height")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "screen_info.width")
  }
}
