package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.vungle

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json.{JsObject, JsonParser}

class Adx(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body.nonEmpty) {
      JsonParser(body).asJsObject()
    } else {
      JsObject()
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {
    // no query parameter seen
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    //print(bodyValues.prettyPrint)
    addPii(PiiTypes.APP_ID, "app.bundle")
    addPii(PiiTypes.ID_OTHER, "app.id")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR, "device.carrier")
    // IOS
    addPii(PiiTypes.DEVICE_BATTERY_LEVEL, "device.ext.vungle.ios.battery_level")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS,
           "device.ext.vungle.ios.battery_state")
    addPii(PiiTypes.DEVICE_NETWORK_CONNECTION_TYPE,
           "device.ext.vungle.ios.connection_type")
    addPii(PiiTypes.ID_VENDOR_ADID, "device.ext.vungle.ios.idfa")
    addPii(PiiTypes.ID_VENDOR_ADID, "device.ext.vungle.ios.idfv")
    addPii(PiiTypes.DEVICE_LANGUAGE, "device.ext.vungle.ios.language")
    addPii(PiiTypes.TIME_ZONE, "device.ext.vungle.ios.time_zone")
    addPii(PiiTypes.DEVICE_VOLUME, "device.ext.vungle.ios.volume_level")
    // Android
    addPii(PiiTypes.DEVICE_BATTERY_LEVEL,
           "device.ext.vungle.android.battery_level")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS,
           "device.ext.vungle.android.battery_state")
    addPii(PiiTypes.DEVICE_NETWORK_CONNECTION_TYPE,
           "device.ext.vungle.android.connection_type")
    addPii(PiiTypes.ID_VENDOR_ADID, "device.ext.vungle.android.gaid")
    addPii(PiiTypes.DEVICE_LANGUAGE, "device.ext.vungle.android.language")
    addPii(PiiTypes.TIME_ZONE, "device.ext.vungle.android.time_zone")
    addPii(PiiTypes.DEVICE_VOLUME, "device.ext.vungle.android.volume_level")

    addPii(PiiTypes.ID_VENDOR_ADID, "device.ifa")
    addPii(PiiTypes.DEVICE_MAKER, "device.make")
    addPii(PiiTypes.DEVICE_MODEL, "device.model")
    addPii(PiiTypes.DEVICE_OS_TYPE, "device.os")
    addPii(PiiTypes.DEVICE_OS_VERSION, "device.osv")
    addPii(PiiTypes.USER_AGEND, "device.ua")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "device.w")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "device.h")
  }

  override protected def parseHeader(implicit headerValues: JsObject,
                                     PIILocation: PIILocation): Unit = {
    super.parseHeader(headerValues, PIILocation)
    addPii(PiiTypes.APP_ID, "X-VUNGLE-BUNDLE-ID")
  }
}
