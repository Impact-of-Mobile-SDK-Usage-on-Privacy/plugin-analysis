package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.applovin

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.{
  EndpointParser,
  HeaderParser
}
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

object AppLovinEndpoints extends EndpointParser {

  private val KNOWN_HOSTS = Set(
    "view.adjust.com",
    "img.applovin.com",
    "ms4.applovin.com",
    "prod-mediate-events.applovin.com",
    "pdn.applovin.com",
    "stage-img.applovin.com",
    "stage-mediate-events.applovin.com",
    "edge.safedk.com",
    "impression.appsflyer.com", //this occurred when using AppLovin thus this is here
    "ms.applovin.com",
    "rt.applovin.com",
    "d.applovin.com",
    "app.adjust.com",
    "res1.applovin.com",
    "assets.applovin.com",
  )

  override def matches(request: Request): Boolean = {
    KNOWN_HOSTS.contains(request.host)
  }

  override def deploy(request: Request): List[PII] = {
    request.host match {
      case "img.applovin.com" =>
        new HeaderParser(request).getPii
      case "pdn.applovin.com" =>
        new HeaderParser(request).getPii
      case "assets.applovin.com" | "res1.applovin.com" |
          "stage-img.applovin.com" =>
        new HeaderParser(request).getPii
      case "view.adjust.com" =>
        new HeaderParser(request).getPii
      case "impression.appsflyer.com" | "app.adjust.com" =>
        new Impressions(request).getPii
      case "ms4.applovin.com" =>
        new MS4(request).getPii
      case "prod-mediate-events.applovin.com" =>
        new Bcode(request).getPii
      case "stage-mediate-events.applovin.com" =>
        new Bcode(request).getPii
      case "edge.safedk.com" =>
        new MsApplovin(request).getPii
      case "ms.applovin.com" =>
        new MsApplovin(request).getPii
      case "rt.applovin.com" =>
        new MsApplovin(request).getPii
      case "d.applovin.com" =>
        new MsApplovin(request).getPii
    }
  }

}
