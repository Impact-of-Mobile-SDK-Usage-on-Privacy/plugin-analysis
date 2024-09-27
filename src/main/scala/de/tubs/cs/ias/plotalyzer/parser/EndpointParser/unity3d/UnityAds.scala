package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.unity3d

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.{BODY, PIILocation, QUERY}
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import de.tubs.cs.ias.plotalyzer.parser.{PIILocations, PIIParser}
import spray.json.{JsObject, JsonParser}

class UnityAds(override val request: Request) extends PIIParser() {
  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    Option(body) match {
      case Some(value) if value.nonEmpty => JsonParser(value).asJsObject()
      case _                             => JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    location: PIILocation = QUERY): Unit = {
    implicit val location: PIILocations.Value = PIILocations.QUERY
    addPii(PiiTypes.DEVICE_MODEL, "deviceModel")
    addPii(PiiTypes.DEVICE_OS_VERSION, "osVersion")
    addPii(PiiTypes.DEVICE_OS_API_LEVEL, "apiLevel")
    addPii(PiiTypes.DEVICE_SCREEN_SIZE, "screenSize")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "screenHeight")
    addPii(PiiTypes.DEVICE_NETWORK_CONNECTION_TYPE, "connectionType")
    addPii(PiiTypes.DEVICE_MAKER, "deviceMaker")
    addPii(PiiTypes.DEVICE_STORES, "stores")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "screenWidth")
    addPii(PiiTypes.DEVICE_SCREEN_SIZE, "screenSize")
    addPii(PiiTypes.ID_UUID, "idfi")
    addPii(PiiTypes.ID_GLOBAL_ADID, "advertisingTrackingId")
    addPii(PiiTypes.DEVICE_OS_TYPE, "platform")
    addPii(PiiTypes.DEVICE_SCREEN_DENSITY, "screenDensity")
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   location: PIILocation = BODY): Unit = {
    implicit val location: PIILocations.PIILocation = PIILocations.BODY
    addPii(PiiTypes.DEVICE_BATTERY_LEVEL, "batteryLevel")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS, "batteryStatus")
    addPii(PiiTypes.APP_ID, "bundleId")
    addPii(PiiTypes.APP_VERSION, "bundleVersion")
    addPii(PiiTypes.DEVICE_SPACE_FREE, "deviceFreeSpace")
    addPii(PiiTypes.DEVICE_NAME, "deviceName")

    addPii(PiiTypes.DEVICE_BATTERY_LEVEL, "ext.device_battery_level")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS, "ext.device_battery_charging")
    addPii(PiiTypes.DEVICE_OS_ROOTED, "ext.ios_jailbroken")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "ext.iu_sizes")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "ext.iu_sizes")
    addPii(PiiTypes.DEVICE_MODEL, "ext.mobile_device_submodel")

    addPii(PiiTypes.DEVICE_FREE_MEMORY, "freeMemory")
    addPii(PiiTypes.DEVICE_LANGUAGE, "language")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "networkOperatorName")
    addPii(PiiTypes.ID_UUID, "projectId")
    addPii(PiiTypes.TIME_ZONE, "timeZone")
    addPii(PiiTypes.DEVICE_SPACE_TOTAL, "totalSpace")
    addPii(PiiTypes.USER_AGEND, "webviewUa")
    addPii(PiiTypes.ID_VENDOR_ADID, "idfv")
    addPii(PiiTypes.DEVICE_LANGUAGE, "locales")
    addPii(PiiTypes.DEVICE_IS_SIMULATOR, "simulator")
  }

}
