package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.unity3d

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.{
  EndpointParser,
  HeaderParser
}
import de.tubs.cs.ias.plotalyzer.parser.pii.PII
import wvlet.log.LogSupport

object Unity3DEndpoints extends EndpointParser with LogSupport {

  private val KNOWN_HOSTS = Set(
    "httpkafka.unityads.unity3d.com",
    "cdn-creatives-prd.acquire.unity3dusercontent.com",
    "configv2.unityads.unity3d.com",
    "config.unityads.unity3d.com",
    "events.stg.mz.internal.unity3d.com",
    "auction.unityads.unity3d.com",
    "auction-load.unityads.unity3d.com",
    "auction-banner.unityads.unity3d.com",
    "sdk-diagnostics.prd.mz.internal.unity3d.com",
    "events.mz.unity3d.com",
    "cdn-creatives-akamaistls-prd.acquire.unity3dusercontent.com",
    "cdn-store-icons-akamai-prd.unityads.unity3d.com"
  )

  def matches(request: Request): Boolean = {
    val ret = KNOWN_HOSTS.contains(request.host)
    ret
  }

  def deploy(request: Request): List[PII] = {
    request.host match {
      case "httpkafka.unityads.unity3d.com" => new UnityEvents(request).getPii
      case "events.mz.unity3d.com" =>
        new UnityEvents(request).getPii
      case "cdn-creatives-prd.acquire.unity3dusercontent.com" =>
        new HeaderParser(request).getPii
      case "configv2.unityads.unity3d.com"
          if request.getPathWithQuery.startsWith("/webview") =>
        new UnityConfig(request).getPii
      case "config.unityads.unity3d.com"
          if request.getPathWithQuery.startsWith("/webview") =>
        new UnityConfig(request).getPii
      case "events.stg.mz.internal.unity3d.com" =>
        new HeaderParser(request).getPii
      case "auction.unityads.unity3d.com"        => new UnityAds(request).getPii
      case "auction-load.unityads.unity3d.com"   => new UnityAds(request).getPii
      case "auction-banner.unityads.unity3d.com" => new UnityAds(request).getPii
      case "sdk-diagnostics.prd.mz.internal.unity3d.com" =>
        new UnityDiagnostics(request).getPii
      case "cdn-creatives-akamaistls-prd.acquire.unity3dusercontent.com" =>
        new HeaderParser(request).getPii
      case "cdn-store-icons-akamai-prd.unityads.unity3d.com" =>
        new HeaderParser(request).getPii
    }
  }

}
