package de.tubs.cs.ias.plotalyzer.parser

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.admob.AdMobEndpoints
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.amplitude.AmplitudeEndpoints
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.applovin.AppLovinEndpoints
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.appsflyer.AppsFlyerEndpoints
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.facebook.FacebookEndpoints
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.firebase.FirebaseEndpoints
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.flurry.FlurryEndpoints
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.unity3d.Unity3DEndpoints
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.vungle.VungleEndpoints
import de.tubs.cs.ias.plotalyzer.parser.pii.PII
import de.tubs.cs.ias.plotalyzer.parser.util.JsStringDebugWrapper.BadJsStringValue
import wvlet.log.LogSupport

object EndpointParserCollection extends LogSupport {

  def deploy(requests: List[Request]): List[PII] = {
    requests.flatMap(deploy)
  }

  def deploy(request: Request): List[PII] = {
    try {
      if (Unity3DEndpoints.matches(request)) {
        Unity3DEndpoints.deploy(request)
      } else if (FlurryEndpoints.matches(request)) {
        FlurryEndpoints.deploy(request)
      } else if (AdMobEndpoints.matches(request)) {
        AdMobEndpoints.deploy(request)
      } else if (FirebaseEndpoints.matches(request)) {
        FirebaseEndpoints.deploy(request)
      } else if (FacebookEndpoints.matches(request)) {
        FacebookEndpoints.deploy(request)
      } else if (AppsFlyerEndpoints.matches(request)) {
        warn(
          "currently we do not understand AppsFlyer data streams due to obfuscation")
        AppsFlyerEndpoints.deploy(request)
      } else if (AppLovinEndpoints.matches(request)) {
        AppLovinEndpoints.deploy(request)
      } else if (AmplitudeEndpoints.matches(request)) {
        AmplitudeEndpoints.deploy(request)
      } else if (VungleEndpoints.matches(request)) {
        VungleEndpoints.deploy(request)
      } else {
        List()
      }
    } catch {
      case x: BadJsStringValue =>
        print(x.getMessage)
        print(x.getStackTrace.mkString("\n"))
        throw x
      case x: Throwable =>
        print(x.getMessage)
        print(x.getStackTrace.mkString("\n"))
        List()
    }
  }

}
