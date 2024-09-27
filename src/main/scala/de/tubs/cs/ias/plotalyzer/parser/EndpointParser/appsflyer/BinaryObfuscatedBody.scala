package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.appsflyer

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import de.tubs.cs.ias.plotalyzer.parser.util.JsStringDebugWrapper
import spray.json.JsObject

import java.util.Base64

class BinaryObfuscatedBody(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (binary != null && binary.nonEmpty) {
      JsObject(
        "obfuscated" -> JsStringDebugWrapper.create(
          Base64.getEncoder.encodeToString(binary)))
    } else {
      JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.APP_ID, "app_id")
    addPii(PiiTypes.ID_OTHER, "uid")
    addPii(PiiTypes.DEVICE_OS_VERSION, "os_version")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.OBFUSCATED, "obfuscated")
  }
}
