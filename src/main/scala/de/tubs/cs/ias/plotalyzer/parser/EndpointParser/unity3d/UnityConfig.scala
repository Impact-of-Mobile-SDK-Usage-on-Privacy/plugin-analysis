package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.unity3d

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.{BODY, PIILocation, QUERY}
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import de.tubs.cs.ias.plotalyzer.parser.{PIILocations, PIIParser}
import spray.json.{JsObject, JsonParser}

// URL ends on /webview/
class UnityConfig(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    Option(body) match {
      case Some(value) if value.nonEmpty => JsonParser(value).asJsObject()
      case _                             => JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    location: PIILocation = QUERY): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   location: PIILocation = BODY): Unit = {
    implicit val location: PIILocations.Value = PIILocations.BODY
    addPii(PiiTypes.ID_UUID, "idfi")
    addPii(PiiTypes.DEVICE_OS_TYPE, "platform")
    addPii(PiiTypes.ID_OTHER, "unifiedconfig#data#gameSessionId")
    addPii(PiiTypes.ID_OTHER, "androidFingerprint")
    addPii(PiiTypes.DEVICE_OS_API_LEVEL, "apiLevel")

    addPii(PiiTypes.DEVICE_BATTERY_LEVEL, "batteryLevel")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS, "batteryStatus")

    addPii(PiiTypes.APP_ID, "bundleId")
    addPii(PiiTypes.APP_VERSION, "bundleVersion")
    addPii(PiiTypes.DEVICE_NETWORK_CONNECTION_TYPE, "connectionType")
    addPii(PiiTypes.DEVICE_ELAPSED_TIME, "deviceElapsedRealtime")
    addPii(PiiTypes.DEVICE_SPACE_FREE, "deviceFreeSpace")
    addPii(PiiTypes.DEVICE_MAKER, "deviceMake")
    addPii(PiiTypes.DEVICE_MODEL, "deviceModel")
    addPii(PiiTypes.DEVICE_NAME, "deviceName")
    addPii(PiiTypes.DEVICE_FREE_MEMORY, "freeMemory")
    addPii(PiiTypes.DEVICE_UPTIME, "deviceUpTime")
    addPii(PiiTypes.ID_UUID, "idfi")
    addPii(PiiTypes.DEVICE_LANGUAGE, "language")
    addPii(PiiTypes.DEVICE_LANGUAGE, "localeList")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "networkOperatorName")
    addPii(PiiTypes.DEVICE_OS_VERSION, "osVersion")
    addPii(PiiTypes.DEVICE_OS_ROOTED, "rooted")
    addPii(PiiTypes.DEVICE_SCREEN_DENSITY, "screenDensity")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "screenHeight")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "screenWidth")
    addPii(PiiTypes.ID_UUID, "sessionId")
    addPii(PiiTypes.SYSTEM_BOOT_TIME, " systemBootTime")
    addPii(PiiTypes.DEVICE_TOTAL_MEMORY, "totalMemory")
    addPii(PiiTypes.DEVICE_SPACE_TOTAL, "totalSpace")
    addPii(PiiTypes.DEVICE_STORES, "stores")
    addPii(PiiTypes.TIME_ZONE, "timeZone")
    //based on the only zero values of IOS
    addPii(PiiTypes.ID_GLOBAL_ADID, "unifiedconfig#pii#advertisingTrackingId")
    addPii(PiiTypes.ID_VENDOR_ADID, "unifiedconfig#pii#vendorIdentifier")
    addPii(PiiTypes.USER_AGEND, "webviewUa")
    addPii(PiiTypes.DEVICE_VOLUME, "volume")
    addPii(PiiTypes.DEVICE_CPU, "cpuCount")
    addPii(PiiTypes.DEVICE_IS_SIMULATOR, "simulator")
  }

}
