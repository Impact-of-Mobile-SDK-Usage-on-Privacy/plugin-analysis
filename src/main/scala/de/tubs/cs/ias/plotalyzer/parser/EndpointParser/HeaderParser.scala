package de.tubs.cs.ias.plotalyzer.parser.EndpointParser

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import spray.json.JsObject

class HeaderParser(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = JsObject()

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {}

}
