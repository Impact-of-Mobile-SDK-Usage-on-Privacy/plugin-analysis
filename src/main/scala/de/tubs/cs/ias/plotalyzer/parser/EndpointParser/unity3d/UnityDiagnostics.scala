package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.unity3d

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsObject, JsonParser}

class UnityDiagnostics(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body.nonEmpty) {
      JsonParser(body).asJsObject()
    } else {
      JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.DEVICE_MAKER, "deviceMake")
    addPii(PiiTypes.DEVICE_MODEL, "deviceModel")
    addPii(PiiTypes.ID_OTHER, "gameId")
    addPii(PiiTypes.ID_UUID, "shSid")
    addPii(PiiTypes.DEVICE_LANGUAGE, "t.iso")
    addPii(PiiTypes.DEVICE_OS_TYPE, "t.plt")
    addPii(PiiTypes.DEVICE_OS_VERSION, "t.system")
  }
}
