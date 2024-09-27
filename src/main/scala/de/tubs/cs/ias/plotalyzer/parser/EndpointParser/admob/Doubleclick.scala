package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.admob

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.{BODY, PIILocation, QUERY}
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsObject, JsonParser}

class Doubleclick(override val request: Request) extends PIIParser() {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body != null && body.nonEmpty) {
      JsonParser(body).asJsObject()
    } else {
      if (binary != null && binary.length > 0) {
        println("BODY but no string")
      }
      JsObject()
    }
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   location: PIILocation = BODY): Unit = {}

  override protected def parseQuery(implicit queryValues: JsObject,
                                    location: PIILocation = QUERY): Unit = {
    addPii(PiiTypes.ID_OTHER, "loeid")
    addPii(PiiTypes.DEVICE_MODEL, "submodel")
    addPii(PiiTypes.ID_OTHER, "eid")
    addPii(PiiTypes.DEVICE_OS_VERSION, "os_version")
    addPii(PiiTypes.IAB_STRING, "gdpr_consent")
    addPii(PiiTypes.APP_ID, "an")
    addPii(PiiTypes.APP_VERSION, "an") //version is concatenated
    addPii(PiiTypes.APP_ID, "app_name") //version is concatenated
    addPii(PiiTypes.ID_OTHER, "fbs_aeid")
  }

}
