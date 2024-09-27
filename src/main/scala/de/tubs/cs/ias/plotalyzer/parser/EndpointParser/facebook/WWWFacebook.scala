package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.facebook

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsObject, JsString, JsonParser}

import java.net.URLDecoder

class WWWFacebook(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body.startsWith("payload=")) {
      var ret = JsonParser(URLDecoder.decode(body.substring("payload=".length))).asJsObject
      if (ret.fields.contains("context") && ret
            .fields("context")
            .asJsObject
            .fields
            .contains("ANALOG")) {
        val analog = JsonParser(
          URLDecoder.decode(
            ret
              .fields("context")
              .asJsObject
              .fields("ANALOG")
              .asInstanceOf[JsString]
              .value))
        ret = JsObject(
          ret.fields ++ Map(
            "context" -> JsObject(
              ret.fields("context").asJsObject.fields ++ Map("ANALOG" -> analog)
            ))
        )
      }
      ret
    } else {
      if (binary.nonEmpty) {
        error(s"missing out on binary ${binary.length}")
      }
      JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.DEVICE_ACCELEROMETER_X, "context.ANALOG.accelerometer_x")
    addPii(PiiTypes.DEVICE_ACCELEROMETER_Y, "context.ANALOG.accelerometer_y")
    addPii(PiiTypes.DEVICE_ACCELEROMETER_Z, "context.ANALOG.accelerometer_z")
    addPii(PiiTypes.DEVICE_BATTERY_LEVEL, "context.ANALOG.battery")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS, "context.ANALOG.charging")
    addPii(PiiTypes.DEVICE_SPACE_FREE, "context.ANALOG.free_space")
    addPii(PiiTypes.DEVICE_FREE_MEMORY, "context.ANALOG.available_memory")
    addPii(PiiTypes.DEVICE_ROTATION_X, "context.ANALOG.rotation_x")
    addPii(PiiTypes.DEVICE_ROTATION_Y, "context.ANALOG.rotation_y")
    addPii(PiiTypes.DEVICE_ROTATION_Z, "context.ANALOG.rotation_z")
    addPii(PiiTypes.DEVICE_TOTAL_MEMORY, "context.ANALOG.total_memory")
    addPii(PiiTypes.APP_ID, "context.APPNAME")
    addPii(PiiTypes.APP_ID, "context.BUNDLE")
    addPii(PiiTypes.APP_VERSION, "context.APPVERS")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "context.CARRIER")
    addPii(PiiTypes.ID_UUID, "context.CLIENT_REQUEST_ID")
    addPii(PiiTypes.ID_GLOBAL_ADID, "context.IDFA")
    addPii(PiiTypes.DEVICE_MAKER, "context.MAKE")
    addPii(PiiTypes.DEVICE_MODEL, "context.MODEL")
    addPii(PiiTypes.DEVICE_LANGUAGE, "context.LOCALE")
    addPii(PiiTypes.DEVICE_OS_TYPE, "context.OS")
    addPii(PiiTypes.DEVICE_OS_VERSION, "context.OSVERS")
    addPii(PiiTypes.DEVICE_OS_ROOTED, "context.ROOTED")
    addPii(PiiTypes.ID_UUID, "context.SESSION_ID")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "context.SCREEN_WIDTH")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "context.SCREEN_HEIGHT")
  }
}
