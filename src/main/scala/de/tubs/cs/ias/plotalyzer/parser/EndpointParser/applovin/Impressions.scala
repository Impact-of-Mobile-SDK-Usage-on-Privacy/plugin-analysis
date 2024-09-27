package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.applovin

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.JsObject

class Impressions(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body.nonEmpty) {
      warn(body)
    } else if (binary.nonEmpty) {
      warn(s"missing binary of size ${binary.length}")
    }
    JsObject()
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_OTHER, "clickid")
    addPii(PiiTypes.ID_OTHER, "af_siteid")
    addPii(PiiTypes.ID_OTHER, "af_c_id")
    addPii(PiiTypes.IP_LOCAL_GATEWAY, "af_ip")
    addPii(PiiTypes.IP_LOCAL_GATEWAY, "ip_address")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {}

}
