package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.vungle

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.JsObject

class Kochava(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    JsObject() //we did not observe a body
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.LATITUDE, "user_lat") // the observed values resided in the USA though
    addPii(PiiTypes.LONGITUDE, "user_lon") // the observed values resided in the USA though
    addPii(PiiTypes.DEVICE_OS_TYPE, "device_os")
    addPii(PiiTypes.ID_OTHER, "impression_id")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {}

}
