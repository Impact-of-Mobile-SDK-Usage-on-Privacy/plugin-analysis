package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.unity3d

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.{BODY, PIILocation, QUERY}
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import de.tubs.cs.ias.plotalyzer.parser.{PIILocations, PIIParser}
import spray.json.{JsObject, JsonParser}

class UnityEvents(override val request: Request) extends PIIParser() {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    request.content
      .split("\n")
      .toList
      .filter(_.nonEmpty)
      .map { string =>
        try {
          Some(JsonParser(string).asJsObject)
        } catch {
          case _: Throwable => None
        }
      }
      .filter(_.nonEmpty)
      .map(_.get)
      .find { obj =>
        obj.fields.contains("common")
      } match {
      case Some(value) => value.fields("common").asJsObject
      case None        => JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    location: PIILocation = QUERY): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   location: PIILocation = BODY): Unit = {
    implicit val location: PIILocations.PIILocation = PIILocations.BODY
    addPii(PiiTypes.APP_ID, "client.bundleId")
    addPii(PiiTypes.APP_VERSION, "client.bundleVersion")
    addPii(PiiTypes.DEVICE_OS_TYPE, "client.platform")

    addPii(PiiTypes.COUNTRY, "country")

    addPii(PiiTypes.DEVICE_MAKER, "device.deviceMaker")
    addPii(PiiTypes.DEVICE_MODEL, "device.deviceModel")
    addPii(PiiTypes.DEVICE_OS_VERSION, "device.osVersion")
    addPii(PiiTypes.DEVICE_OS_API_LEVEL, "device.apiLevel")
    addPii(PiiTypes.DEVICE_BATTERY_LEVEL, "device.batteryLevel")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS, "device.batteryStatus")
    addPii(PiiTypes.DEVICE_NETWORK_CONNECTION_TYPE, "device.connectionType")
    addPii(PiiTypes.DEVICE_VOLUME, "device.deviceVolume")
    addPii(PiiTypes.DEVICE_FREE_MEMORY, "device.freeMemory")
    addPii(PiiTypes.DEVICE_SPACE_EXTERNAL_FREE, "device.freeSpaceExternal")
    addPii(PiiTypes.DEVICE_SPACE_INTERNAL_FREE, "device.freeSpaceInternal")
    addPii(PiiTypes.DEVICE_HEADSET, "device.headset")
    addPii(PiiTypes.DEVICE_LANGUAGE, "device.language")
    addPii(PiiTypes.DEVICE_OS_ROOTED, "device.rooted")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "device.networkOperatorName")
    addPii(PiiTypes.DEVICE_SCREEN_BRIGHTNESS, "device.screenBrightness")
    addPii(PiiTypes.DEVICE_SCREEN_DENSITY, "device.screenDensity")
    addPii(PiiTypes.DEVICE_SCREEN_LAYOUT, "device.screenLayout")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "device.screenHeight")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "device.screenWidth")
    addPii(PiiTypes.TIME_ZONE, "device.timeZone")
    addPii(PiiTypes.DEVICE_TOTAL_MEMORY, "device.totalMemory")
    addPii(PiiTypes.DEVICE_SPACE_EXTERNAL_TOTAL, "device.totalSpaceExternal")
    addPii(PiiTypes.DEVICE_SPACE_INTERNAL_TOTAL, "device.totalSpaceInternal")
    addPii(PiiTypes.USER_AGEND, "device.userAgent")
  }
}
