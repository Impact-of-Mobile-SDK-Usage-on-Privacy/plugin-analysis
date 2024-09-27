package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.vungle

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsObject, JsonParser}

class Events(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (Option(body).nonEmpty && body.nonEmpty) {
      try {
        JsonParser(body).asJsObject
      } catch {
        case _: Exception =>
          warn("cannot json parse " + body)
          JsObject()
      }
    } else if (binary.length > 0) {
      warn("missing binary of length " + binary.length)
      JsObject()
    } else {
      JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_VENDOR_ADID, "vid") // later on the json states that vid is the IDFV
    addPii(PiiTypes.DEVICE_OS_TYPE, "os")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    // we did not observe a parsable body
  }

  override protected def parseHeader(implicit headerValues: JsObject,
                                     PIILocation: PIILocation): Unit = {
    super.parseHeader(headerValues, PIILocation)
    addPii(PiiTypes.APP_ID, "X-VUNGLE-BUNDLE-ID")
  }
}
