package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.appsflyer

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.{
  EndpointParser,
  HeaderParser
}
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

object AppsFlyerEndpoints extends EndpointParser {

  private val KNOWN_HOSTS = Set(
    "2on2q8-conversions.appsflyersdk.com",
    "2on2q8-inapps.appsflyersdk.com",
    "2on2q8-cdn-settings.appsflyersdk.com",
    "gcgv3a-inapps.appsflyersdk.com",
    "gcgv3a-cdn-settings.appsflyersdk.com",
    "gcgv3a-attr.appsflyersdk.com",
    "gcgv3a-skadsdk.appsflyersdk.com",
    "gcgv3a-dynamic-config-api.appsflyersdk.com",
    "gcgv3a-conversions.appsflyersdk.com",
    "fjfock-conversions.appsflyersdk.com",
    "fjfock-cdn-settings.appsflyersdk.com",
    "fjfock-inapps.appsflyersdk.com"
  )

  override def matches(request: Request): Boolean = {
    KNOWN_HOSTS.contains(request.host)
  }

  override def deploy(request: Request): List[PII] = {
    request.host match {
      case "2on2q8-conversions.appsflyersdk.com" |
          "fjfock-inapps.appsflyersdk.com" =>
        new BinaryObfuscatedBody(request).getPii
      case "2on2q8-inapps.appsflyersdk.com" |
          "fjfock-conversions.appsflyersdk.com" |
          "fjfock-cdn-settings.appsflyersdk.com" =>
        new BinaryObfuscatedBody(request).getPii
      case "2on2q8-cdn-settings.appsflyersdk.com" =>
        // no other content
        new HeaderParser(request).getPii
      case "gcgv3a-inapps.appsflyersdk.com" =>
        new BinaryObfuscatedBody(request).getPii
      case "gcgv3a-cdn-settings.appsflyersdk.com" =>
        // no other content
        new HeaderParser(request).getPii
      case "gcgv3a-attr.appsflyersdk.com" =>
        new BinaryObfuscatedBody(request).getPii
      case "gcgv3a-skadsdk.appsflyersdk.com" =>
        // no other content
        new HeaderParser(request).getPii
      case "gcgv3a-dynamic-config-api.appsflyersdk.com" =>
        // no other content
        new HeaderParser(request).getPii
      case "gcgv3a-conversions.appsflyersdk.com" =>
        new BinaryObfuscatedBody(request).getPii
    }
  }

}
