package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.facebook

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.util.DeepMerge
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import de.tubs.cs.ias.plotalyzer.parser.util.JsStringDebugWrapper
import spray.json.{JsArray, JsObject, JsString, JsonParser}

import java.net.URLDecoder

class GraphFacebook(override val request: Request) extends PIIParser {

  private def decodeExtInfo(str: String): JsObject = {
    JsonParser(URLDecoder.decode(str)) match {
      case array: JsArray =>
        JsObject(
          array.elements.indices.map { elem =>
            elem.toString -> array.elements(elem)
          }.toMap
        )
      case x =>
        error(s"unexpected extinfo value $x")
        JsObject()
    }
  }

  private def convertActivities(body: String): JsObject = {
    try {
      val ret = JsonParser(body).asJsObject
      val extinfoArray: JsObject = decodeExtInfo(
        URLDecoder.decode(ret.fields("extinfo").asInstanceOf[JsString].value))
      val rtrn = JsObject(
        ret.fields ++ Map("extinfo" -> extinfoArray)
      )
      rtrn
    } catch {
      case _: Throwable =>
        val ret = super.convertQuery(body)
        val extInfo = decodeExtInfo(
          ret.fields("extinfo").asInstanceOf[JsString].value)
        JsObject(
          ret.fields ++ Map("extinfo" -> extInfo)
        )
    }
  }

  private def convertVersionOnly(body: String): JsObject = {
    val batchJson = JsonParser(body)
    val batchArrayParsed = JsonParser(
      batchJson.asJsObject.fields("batch").asInstanceOf[JsString].value)
    val subObjects = batchArrayParsed.asInstanceOf[JsArray].elements.map {
      subElement =>
        val ret = super.convertQuery(
          subElement.asJsObject
            .fields("relative_url")
            .asInstanceOf[JsString]
            .value
            .split('?')
            .tail
            .head)
        ret
    }
    val rtrn = DeepMerge.merge(subObjects: _*)
    rtrn
  }

  private def convertSimpleQueryString(body: String): JsObject = {
    var ret = super.convertQuery(body)
    if (ret.fields.contains("extinfo")) {
      warn(ret.fields("extinfo").prettyPrint)
    }
    if (ret.fields.contains("ANALOG")) {
      val analog = JsonParser(
        URLDecoder.decode(ret.fields("ANALOG").asInstanceOf[JsString].value))
      ret = JsObject(
        ret.fields ++ Map("ANALOG" -> analog)
      )
    }
    if (ret.fields.contains("VALPARAMS")) {
      val analog = JsonParser(
        URLDecoder.decode(ret.fields("VALPARAMS").asInstanceOf[JsString].value))
      ret = JsObject(
        ret.fields ++ Map("VALPARAMS" -> analog)
      )
    }
    if (ret.fields.contains("USER_AGENT")) {
      ret = JsObject(
        ret.fields ++ Map(
          "USER_AGENT" -> JsStringDebugWrapper.create(URLDecoder.decode(
            ret.fields("USER_AGENT").asInstanceOf[JsString].value)))
      )
    }
    ret
  }

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (request.path != null && request.path.matches("/v\\d{1,2}.0$")) {
      val ret = convertVersionOnly(body)
      ret
    } else if (request.path != null && request.getPath.matches(
                 "/v\\d{1,2}.*/[0-9]+/activities")) {
      val ret = convertActivities(body)
      ret
    } else if (body != null && body.contains('&')) {
      val ret = convertSimpleQueryString(body)
      ret
    } else {
      if (binary != null && binary.nonEmpty) {
        warn(s"missing binary of size ${binary.length}")
      }
      JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.DEVICE_OS_TYPE, "platform")
    addPii(PiiTypes.DEVICE_OS_TYPE, "sdk")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.ID_UUID, "anon_id")
    addPii(PiiTypes.ID_UUID, "ANON_ID")
    addPii(PiiTypes.DEVICE_OS_TYPE, "sdk")
    addPii(PiiTypes.DEVICE_OS_VERSION, "os_version")
    addPii(PiiTypes.ID_GLOBAL_ADID, "IDFA")
    addPii(PiiTypes.DEVICE_VOLUME, "VOLUME")
    addPii(PiiTypes.USER_AGEND, "USER_AGENT")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "extinfo.8")
    addPii(PiiTypes.DEVICE_MODEL, "extinfo.5")
    addPii(PiiTypes.TIME_ZONE, "extinfo.15")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "extinfo.10")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "extinfo.9")
    addPii(PiiTypes.DEVICE_LANGUAGE, "extinfo.6")
    addPii(PiiTypes.APP_ID, "extinfo.1")
    addPii(PiiTypes.ID_GLOBAL_ADID, "IDFA")
    addPii(PiiTypes.ID_UUID, "SESSION_ID")
    addPii(PiiTypes.APP_ID, "APPNAME")
    addPii(PiiTypes.APP_ID, "application_package_name")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "SCREEN_WIDTH")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "SCREEN_HEIGHT")
    addPii(PiiTypes.DEVICE_OS_VERSION, "OSVERS")
    addPii(PiiTypes.DEVICE_ACCELEROMETER_X, "ANALOG.accelerometer_x")
    addPii(PiiTypes.DEVICE_ACCELEROMETER_Y, "ANALOG.accelerometer_y")
    addPii(PiiTypes.DEVICE_ACCELEROMETER_Z, "ANALOG.accelerometer_z")
    addPii(PiiTypes.DEVICE_BATTERY_LEVEL, "ANALOG.battery")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS, "ANALOG.charging")
    addPii(PiiTypes.DEVICE_SPACE_FREE, "ANALOG.free_space")
    addPii(PiiTypes.DEVICE_ROTATION_X, "ANALOG.rotation_x")
    addPii(PiiTypes.DEVICE_ROTATION_Y, "ANALOG.rotation_y")
    addPii(PiiTypes.DEVICE_ROTATION_Z, "ANALOG.rotation_z")
    addPii(PiiTypes.DEVICE_TOTAL_MEMORY, "ANALOG.total_memory")
    addPii(PiiTypes.DEVICE_MAKER, "MAKE")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "CARRIER")
    addPii(PiiTypes.DEVICE_MODEL, "MODEL")
    addPii(PiiTypes.DEVICE_LANGUAGE, "LOCALE")
    addPii(PiiTypes.DEVICE_OS_TYPE, "OS")
    addPii(PiiTypes.DEVICE_OS_ROOTED, "ROOTED")
    addPii(PiiTypes.ID_UUID, "CLIENT_REQUEST_ID")
    addPii(PiiTypes.APP_ID, "BUNDLE")
    addPii(PiiTypes.DEVICE_VOLUME, "VOLUME")
  }
}
