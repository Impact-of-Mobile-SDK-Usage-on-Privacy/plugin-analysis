package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.vungle

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.{
  EndpointParser,
  HeaderParser
}
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

object VungleEndpoints extends EndpointParser {

  private val KNOWN_HOSTS = Set(
    "events.ads.vungle.com",
    "adx.ads.vungle.com",
    "cdn-lb.vungle.com",
    "config.ads.vungle.com",
    "creatives.smadex.com",
    "static-content-1.smadex.com",
    "us-event.app-install.bid",
    "imp-lb-us2.jampp.com",
    "cdn.jampp.com",
    "imp.control.kochava.com"
  )

  override def deploy(request: Request): List[PII] = {
    request.host match {
      case "events.ads.vungle.com" =>
        new Events(request).getPii
      case "adx.ads.vungle.com" | "config.ads.vungle.com" =>
        new Adx(request).getPii
      case "creatives.smadex.com" | "static-content-1.smadex.com" =>
        new Smadex(request).getPii
      case "cdn-lb.vungle.com" | "cdn.jampp.com" | "us-event.app-install.bid" |
          "imp-lb-us2.jampp.com" =>
        // only header in this request
        new HeaderParser(request).getPii
      case "imp.control.kochava.com" =>
        new Kochava(request).getPii
    }
  }

  override def matches(request: Request): Boolean = {
    KNOWN_HOSTS.contains(request.host)
  }
}
