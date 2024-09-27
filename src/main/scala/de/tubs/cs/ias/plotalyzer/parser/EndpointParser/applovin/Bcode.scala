package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.applovin

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsObject, JsonParser}

class Bcode(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    try {
      JsonParser(body).asJsObject()
    } catch {
      case _: Throwable =>
        JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_OTHER, "id")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.OBFUSCATED, "bcode")
  }

  override protected def parseHeader(implicit headerValues: JsObject,
                                     PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_OTHER, "applovin-ad-unit-id")
  }
}
