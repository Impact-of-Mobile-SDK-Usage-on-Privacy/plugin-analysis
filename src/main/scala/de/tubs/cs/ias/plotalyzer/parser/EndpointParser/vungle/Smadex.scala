package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.vungle

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsObject, JsonParser}

class Smadex(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body.nonEmpty) {
      JsonParser(body).asJsObject
    } else {
      JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_UUID, "req_id")
    addPii(PiiTypes.ID_VENDOR_ADID, "idfa_raw")
    addPii(PiiTypes.ID_VENDOR_ADID, "gaid_raw")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    // we did not observe a body
  }
}
