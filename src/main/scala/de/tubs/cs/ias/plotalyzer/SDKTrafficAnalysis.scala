package de.tubs.cs.ias.plotalyzer

import de.halcony.plotalyzer.database.entities.InterfaceAnalysis
import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.halcony.plotalyzer.plugins.{
  AnalysisContext,
  AnalysisPlugin,
  AnalysisReturn,
  JSONReturn
}
import de.tubs.cs.ias.plotalyzer.parser.EndpointParserCollection
import de.tubs.cs.ias.plotalyzer.parser.pii.PII
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsValue}
import wvlet.log.LogSupport

/** Plugin to analyze the misses where objection+mitmproxy were unable to intercept HTTPS
  *
  * @author Simon Koch
  *
  */
class SDKTrafficAnalysis() extends AnalysisPlugin with LogSupport {

  /** analyzes the requests directed to the same domain
    *
    * {
    *    domains : {
    *      "<domainString>" : {
    *        "<pathAndQuery>" : {
    *            "success" : <INT>,
    *            "overall" : <INT>,
    *            "pii" : {
    *               <dataPoints> : [<PII>,...]
    *            }
    *        }
    *        ....
    *      }
    * }
    *
    * @param requests the requests encountered for a single step within the app
    * @return the results summarized in a json
    */
  private def stepRequestsAnalysis(requests: List[Request]): JsValue = {
    val domains: Map[String, Map[String, (Int, Int, List[PII])]] = {
      requests.filter(_.host != null).groupBy(_.host).map { pair =>
        val pathAndQueries: Map[String, (Int, Int, List[PII])] = {
          pair._2.groupBy(_.getPathWithQuery).map { pair =>
            pair._1 -> (pair._2
              .count(_.error.isEmpty), pair._2.length, EndpointParserCollection
              .deploy(pair._2))
          }
        }
        pair._1 -> pathAndQueries
      }
    }
    JsObject(
      "domains" -> JsObject(
        domains.map {
          case (domain, queryDistinction) =>
            domain -> JsObject(
              queryDistinction.map {
                case (query, triple) =>
                  query -> JsObject(
                    "success" -> JsNumber(triple._1),
                    "overall" -> JsNumber(triple._2),
                    "pii" -> JsArray(triple._3.map(_.toJson).toVector)
                  )
              }
            )
        }
      )
    )
  }

  /** analyzes the requests collected during the test app execution with removed chatter
    *
    * {
    *    "ACTION" : {
    *      <stepRequestAnalysisResult>
    *    },
    *    ...
    * }
    *
    * @param app the interface analysis of the app
    * @return the results summarized in a json
    */
  private def processApp(app: InterfaceAnalysis,
                         chatter: Set[String]): JsValue = {
    val res: Map[String, JsValue] = app.getTrafficCollection
      .filterNot(_.getComment == "NOTHING")
      .map { trafficCollection =>
        val comment = Option(trafficCollection.getComment) match {
          case Some(value) => value
          case None        => "no comment"
        }
        comment -> {
          val ret =
            stepRequestsAnalysis(trafficCollection.getRequests.filterNot(req =>
              chatter.contains(req.host)))
          ret.prettyPrint
          ret
        }
      }
      .toMap
    JsObject(res)
  }

  /** analyzes the mea culpa collected data points and extracts transmitted personal information
    *
    *  {
    *    "chatter" : ["<domain>",...],
    *    "appAnalysis" : {
    *      "<appName>" -> <processAppResult>,
    *      ...
    *    }
    *  }
    *
    * @param context the context for which the analysis takes place (i.e., the measurement)
    * @return the JsValue (hopefully)
    */
  override def analyze(
      context: AnalysisContext): Either[Exception, AnalysisReturn] = {
    try {
      val analysis: List[InterfaceAnalysis] =
        InterfaceAnalysis.get(context.experiment)(context.database)
      val chatter =
        Set(
          "aidc.apple.com",
          "mesu.apple.com",
          "token.safebrowsing.apple",
          "bag.itunes.apple.com",
          "cl4.apple.com",
          "cl3.apple.com",
          "apps.mzstatic.com",
          "gsp57-ssl-revgeo.ls.apple.com",
          "ocsp2.apple.com",
          "ipcdn.apple.com",
          "kt-prod.ess.apple.com",
          "fbs.smoot.apple.com",
          "gsp57-ssl-revgeo.ls.apple.com",
          "gsp57-ssl-locus.ls.apple.com",
          "iphone-ld.apple.com",
          "configuration.apple.com",
          "api.smoot.apple.com",
          "gs-loc.apple.com",
          "configuration.ls.apple.com",
          "keyvalueservice.icloud.com",
          "p114-contacts.icloud.com",
          "p114-caldav.icloud.com",
          "fonts.googleapis.com",
          "fonts.gstatic.com",
          "xgapromomanager-pa.googleapis.com",
          "gmscompliance-pa.googleapis.com",
          "voilatile-pa.googleapis.com",
          "nearbysharing-pa.googleapis.com",
          "playatoms-pa.googleapis.com",
          "cryptauthenrollment.googleapis.com",
          "locationhistory-pa.googleapis.com",
          "safebrowsing.googleapis.com",
          "phonedeviceverification-pa.googleapis.com",
          "clientservices.googleapis.com",
          "federatedcompute-pa.googleapis.com",
          "androidattestationvalidation-pa.googleapis.com",
          "gcs-eu-00002.content-storage-upload.googleapis.com",
          "p63-sharedstreams.icloud.com",
          "ca.iadsdk.apple.com",
          "p114-keyvalueservice.icloud.com",
          "weather-data.apple.com",
          "gsp53-ssl.ls.apple.com",
          "youtubei.googleapis.com",
          "clienttracing-pa.googleapis.com",
          "proactivebackend-pa.googleapis.com",
          "geller-pa.googleapis.com",
          "digitalassetlinks.googleapis.com",
          "android.googleapis.com",
          "infinitedata-pa.googleapis.com",
          "android-context-data.googleapis.com",
          "optimizationguide-pa.googleapis.com",
          "encrypted-tbn0.gstatic.com",
          "lh5.googleusercontent.com",
          "i2.ytimg.com",
          "play-fe.googleapis.com",
          "accounts.google.com",
          "ota.googlezip.net",
          "metrics.icloud.com",
          "www.gstatic.com",
          "play.googleapis.com",
          "update.googleapis.com",
          "www.googleapis.com",
          "dl.google.com",
          "adservice.google.com",
          "instantmessaging-pa.googleapis.com",
          "securitydomain-pa.googleapis.com",
          "cryptauthdevicesync.googleapis.com",
          "auditrecording-pa.googleapis.com",
          "meetings.googleapis.com",
          "android.apis.google.com",
          "init.itunes.apple.com",
          "gsp64-ssl.ls.apple.com",
          "catcher.ikseses.xyz",
          "gateway.icloud.com",
          "p114-fmf.icloud.com",
          "client-api.itunes.apple.com",
          "amp-api-edge.apps.apple.com",
          "pd.itunes.apple.com",
          "publicassets.cdn-apple.com",
          "gsas.apple.com",
          "i.ytimg.com",
          "play.itunes.apple.com",
          "gspe85-ssl.ls.apple.com",
          "xp.apple.com",
          "updates.cdn-apple.com",
          "cl5.apple.com",
          "setup.icloud.com",
          "gsp10-ssl.apple.com",
          "b6b5p6y2.ssl.hwcdn.net",
          "cl2.apple.com",
          "connectivitycheck.gstatic.com",
          "beacons.gvt2.com",
          "www.google.com",
          "edgedl.me.gvt1.com",
        )
      val result: Map[String, JsValue] = analysis.map { ana =>
        ana.getApp.toString() -> {
          val res = processApp(ana, chatter)
          res.prettyPrint
          res
        }
      }.toMap
      Right[Exception, AnalysisReturn](
        JSONReturn(
          JsObject(
            "chatter" -> JsArray(chatter.map(elem => JsString(elem)).toVector),
            "appAnalysis" -> JsObject(result)
          )
        ))
    } catch {
      case x: Exception => Left(x)
    }
  }

}
