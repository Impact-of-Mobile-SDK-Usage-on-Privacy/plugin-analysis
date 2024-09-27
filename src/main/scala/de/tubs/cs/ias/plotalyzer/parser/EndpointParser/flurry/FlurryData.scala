package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.flurry

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations._
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import spray.json
import spray.json.JsObject

import java.io.ByteArrayInputStream
import java.nio.charset.CodingErrorAction
import scala.annotation.tailrec
import scala.io.{Codec, Source}

class FlurryData(override val request: Request) extends PIIParser() {

  @tailrec
  private def extractJsonBlurps(iterator: Iterator[Char],
                                collection: List[Char],
                                blurps: List[String],
                                counter: Int): List[String] = {
    if (iterator.hasNext) {
      val next: Char = iterator.next()
      //print(next)
      next match {
        case '{' =>
          extractJsonBlurps(iterator, '{' :: collection, blurps, counter + 1)
        case '}' =>
          if (counter == 1) {
            extractJsonBlurps(iterator,
                              Nil,
                              ('}' :: collection).reverse.mkString :: blurps,
                              counter = 0)
          } else if (counter > 1) {
            extractJsonBlurps(iterator, '}' :: collection, blurps, counter - 1)
          } else {
            extractJsonBlurps(iterator, collection, blurps, counter)
          }
        case x =>
          if (counter > 0) {
            extractJsonBlurps(iterator, x :: collection, blurps, counter)
          } else {
            extractJsonBlurps(iterator, collection, blurps, counter)
          }
      }
    } else {
      if (collection.nonEmpty) {
        collection.mkString.reverse :: blurps
      } else {
        blurps
      }
    }
  }

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    implicit val codec = Codec("ASCII")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    val brokenJson: String =
      Source.fromInputStream(new ByteArrayInputStream(binary)).mkString
    val blurps: List[String] =
      extractJsonBlurps(brokenJson.iterator, Nil, Nil, counter = 0)
    var ret: JsObject = JsObject()
    var current = blurps.head
    var next: List[String] = blurps.tail
    while (next.nonEmpty) {
      try {
        val parsed: JsObject = json.JsonParser(current).asJsObject()
        ret = JsObject(
          ret.fields ++ parsed.fields
        )
        current = next.head
        next = next.tail
      } catch {
        case _: Throwable =>
          val subblurps = extractJsonBlurps(current.substring(1).iterator,
                                            Nil,
                                            Nil,
                                            counter = 0)
          current = subblurps.head
          next = subblurps.tail ++ next
      }
    }
    ret
  }

  override def parseQuery(implicit queryValues: JsObject,
                          PIILocation: PIILocation = QUERY): Unit = {}

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation = BODY): Unit = {
    addPii(PiiTypes.TIME_ZONE, "fl#timezone#value")
    addPii(PiiTypes.DEVICE_TOTAL_MEMORY,
           "fl#session#property.memory#total#start.value")
    addPii(PiiTypes.DEVICE_FREE_MEMORY,
           "fl#session#property.memory#available#start.value")
    addPii(PiiTypes.DEVICE_SPACE_INTERNAL_TOTAL,
           "fl#session#property.disk#size#total#internal.value")
    addPii(PiiTypes.DEVICE_SPACE_EXTERNAL_TOTAL,
           "fl#session#property.disk#size#total#external.value")
    addPii(PiiTypes.DEVICE_SPACE_INTERNAL_FREE,
           "fl#session#property.disk#size#available#internal.value")
    addPii(PiiTypes.DEVICE_SPACE_EXTERNAL_FREE,
           "fl#session#property.disk#size#available#external.value")
    addPii(PiiTypes.DEVICE_NETWORK_OPERATOR,
           "fl#session#property.carrier#name.value")
    addPii(PiiTypes.SYSTEM_BOOT_TIME, "fl#session#property.boot#time.value")
    addPii(PiiTypes.DEVICE_BATTERY_LEVEL,
           "fl#session#property.battery#remaining#start.value")
    addPii(PiiTypes.DEVICE_BATTERY_STATUS,
           "fl#session#property.battery#charging#start.value")

    addPii(PiiTypes.ID_OTHER, "fl#session#id")
    addPii(PiiTypes.ID_GLOBAL_ADID, "fl#reported#id.AndroidAdvertisingId")
    addPii(PiiTypes.ID_VENDOR_ADID, "fl#reported#id.VendorID")
    addPii(PiiTypes.ID_OTHER, "fl#reported#id.AndroidInstallationId")
    addPii(PiiTypes.ID_OTHER, "fl#reported#id.iOSInstallationId")
    addPii(PiiTypes.DEVICE_LANGUAGE, "fl#language")

    addPii(PiiTypes.DEVICE_OS_VERSION, "fl#device#properties.version#release")
    addPii(PiiTypes.DEVICE_OS_VERSION, "fl#device#properties.device#os#version")
    addPii(PiiTypes.DEVICE_SCREEN_HEIGHT, "fl#device#properties.scr#height")
    addPii(PiiTypes.DEVICE_SCREEN_WIDTH, "fl#device#properties.scr#width")
    addPii(PiiTypes.DEVICE_MODEL, "fl#device#properties.device#model")
    addPii(PiiTypes.DEVICE_MODEL, "fl#device#properties.device#model#1")
    addPii(PiiTypes.DEVICE_MAKER, "fl#device#properties.build#brand")

    addPii(PiiTypes.COUNTRY, "fl#country")
    addPii(PiiTypes.APP_ID, "fl#bundle#id")
    addPii(PiiTypes.APP_VERSION, "fl#app#version")
  }
}
