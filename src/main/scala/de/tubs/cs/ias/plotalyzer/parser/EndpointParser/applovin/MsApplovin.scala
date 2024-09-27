package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.applovin

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.applovin.AppLovinDecode.DecodeException
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import de.tubs.cs.ias.plotalyzer.parser.util.JsStringDebugWrapper
import spray.json.{JsObject, JsString, JsonParser}

import java.net.URLDecoder
import java.util.Base64

class MsApplovin(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body != null && body.nonEmpty) {
      try {
        val ret = AppLovinDecode.rtDecode(body,
                                          AppLovinDecode.ANDROID_SDK_KEY,
                                          AppLovinDecode.ANDROID_IV)
        JsObject(
          JsonParser(ret).asJsObject.fields ++ Map(
            "deobfuscated" -> JsStringDebugWrapper.create(body))
        )
      } catch {
        case DecodeException(msg) =>
          warn(
            s"unable to decode request due to: $msg (this is probably due to the request being from iOS)")
          JsObject("obfuscated" -> JsStringDebugWrapper.create(body))
      }
    } else if (binary != null && binary.nonEmpty) {
      warn(s"we are missing ${binary.length} binary data")
      JsObject(
        "obfuscated" -> JsStringDebugWrapper.create(
          Base64.getEncoder.encodeToString(binary)))
    } else {
      JsObject()
    }
  }

  override protected def convertQuery(query: String): JsObject = {
    val queryJson: JsObject = super.convertQuery(query)
    try {
      if (queryJson.fields.contains("p")) {
        val ret: String =
          AppLovinDecode.rtDecode(
            URLDecoder.decode(
              queryJson.fields("p").asInstanceOf[JsString].value),
            AppLovinDecode.ANDROID_SDK_KEY,
            AppLovinDecode.ANDROID_IV)
        JsObject(
          convertQuery(ret).fields ++ Map(
            "deobfuscated" -> queryJson.fields("p")))
      } else {
        queryJson
      }
    } catch {
      case DecodeException(msg) =>
        warn(
          s"unable to decode request due to: $msg (this is probably due to the request being from iOS)")
        if (queryJson.fields.contains("p")) {
          JsObject("obfuscated" -> queryJson.fields("p"))
        } else {
          JsObject()
        }
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_UUID, "rid")
    addPii(PiiTypes.ID_UUID, "event_id")
    addPii(PiiTypes.DEVICE_VOLUME, "volume")
    addPii(PiiTypes.DEVICE_LANGUAGE, "kb")
    addPii(PiiTypes.DEVICE_LANGUAGE, "locale")
    addPii(PiiTypes.DEVICE_NETWORK_CONNECTION_TYPE, "network")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "carrier")
    addPii(PiiTypes.APP_VERSION, "app_version")
    addPii(PiiTypes.APP_ID, "package_name")
    addPii(PiiTypes.ID_VENDOR_ADID, "idfv")
    addPii(PiiTypes.ID_GLOBAL_ADID, "idfa")
    addPii(PiiTypes.ID_UUID, "compass_random_token")
    addPii(PiiTypes.ID_UUID, "applovin_random_token")
    addPii(PiiTypes.DEVICE_MODEL, "model")
    addPii(PiiTypes.DEVICE_MAKER, "brand")
    addPii(PiiTypes.DEVICE_OS_TYPE, "platform")
    addPii(PiiTypes.OBFUSCATED, "obfuscated")
    addPii(PiiTypes.DEOBFUSCATED, "deobfuscated")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.APP_ID, "app_info.app_name")
    addPii(PiiTypes.APP_ID, "app_info.package_name")
    addPii(PiiTypes.APP_VERSION, "app_info.app_version")
    addPii(PiiTypes.ID_UUID, "app_info.applovin_random_token")
    addPii(PiiTypes.ID_UUID, "app_info.compass_random_token")

    addPii(PiiTypes.DEVICE_MAKER, "device_info.brand")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "device_info.carrier")
    addPii(PiiTypes.DEVICE_LANGUAGE, "device_info.country_code")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "device_info.dy")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "device_info.dx")
    addPii(PiiTypes.ID_GLOBAL_ADID, "device_info.idfa")
    addPii(PiiTypes.ID_VENDOR_ADID, "device_info.idfv")
    addPii(PiiTypes.DEVICE_MODEL, "device_info.model")
    addPii(PiiTypes.DEVICE_NETWORK_CONNECTION_TYPE, "device_info.network")
    addPii(PiiTypes.DEVICE_OS_TYPE, "device_info.type")
    addPii(PiiTypes.DEVICE_OS_VERSION, "device_info.os")

    addPii(PiiTypes.OBFUSCATED, "obfuscated")
    addPii(PiiTypes.DEOBFUSCATED, "deobfuscated")
  }
}
