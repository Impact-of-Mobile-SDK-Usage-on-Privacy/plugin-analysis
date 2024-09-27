package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.amplitude

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.util.DeepMerge
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsArray, JsObject, JsonParser}
import wvlet.log.LogSupport

class ApiAmplitude(override val request: Request)
    extends PIIParser
    with LogSupport {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    try {
      if (body.nonEmpty) {
        val ret = JsonParser(body).asJsObject
        DeepMerge.merge(
          ret
            .fields("events")
            .asInstanceOf[JsArray]
            .elements
            .map(_.asJsObject): _*)
      } else {
        if (binary.nonEmpty) {
          warn(s"we are missing binary ${binary.length}")
        }
        JsObject()
      }
    } catch {
      case _: Throwable =>
        JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_OTHER, "session_id")
    addPii(PiiTypes.ID_UUID, "insert_id")
    addPii(PiiTypes.ID_UUID, "android_app_set_id")
    addPii(PiiTypes.DEVICE_LANGUAGE, "language")
    addPii(PiiTypes.DEVICE_OS_TYPE, "platform")
    addPii(PiiTypes.DEVICE_MAKER, "device_manufacturer")
    addPii(PiiTypes.ID_GLOBAL_ADID, "adid")
    addPii(PiiTypes.ID_UUID, "device_id")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "carrier")
    addPii(PiiTypes.DEVICE_MODEL, "device_model")
    addPii(PiiTypes.DEVICE_OS_TYPE, "os_name")
    addPii(PiiTypes.DEVICE_OS_VERSION, "os_version")
  }
}
