package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.admob

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.EndpointParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

object AdMobEndpoints extends EndpointParser {

  private val KNWON_HOST = Set(
    "googleads.g.doubleclick.net",
    "fundingchoicesmessages.google.com"
  )

  override def matches(request: Request): Boolean = {
    KNWON_HOST.contains(request.host)
  }

  override def deploy(request: Request): List[PII] = {
    request.host match {
      case "googleads.g.doubleclick.net" =>
        new Doubleclick(request).getPii
      case "fundingchoicesmessages.google.com" =>
        new Fundingchoices(request).getPii
    }
  }

}
