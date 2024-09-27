package de.tubs.cs.ias.plotalyzer.parser.EndpointParser

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.{
  BODY,
  HEADER,
  PIILocation,
  QUERY
}
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import spray.json.JsObject

import java.io.{
  BufferedOutputStream,
  ByteArrayInputStream,
  EOFException,
  FileOutputStream
}
import java.nio.charset.CodingErrorAction
import java.util.UUID
import java.util.zip.{GZIPInputStream, ZipException}
import scala.io.{Codec, Source}

class UnknownParser(override val request: Request) extends PIIParser {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    println(s"UNKNOWN ${request.host} // Body Raw")
    println(body)
    if (binary != null && binary.length > 0) {
      println(binary.length)
      val ostream = new BufferedOutputStream(
        new FileOutputStream(s"./unknown.binary.${UUID.randomUUID().toString}"))
      try {
        implicit val codec = Codec("ASCII")
        codec.onMalformedInput(CodingErrorAction.REPLACE)
        codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
        ostream.write(binary)
        println(
          Source.fromInputStream(new ByteArrayInputStream(binary)).mkString)
        val bos = new GZIPInputStream(new ByteArrayInputStream(binary))
        println(scala.io.Source.fromInputStream(bos).mkString)
      } catch {
        case _: ZipException => println("not gzip encoded")
        case _: EOFException => println("not gzip encoded")
      } finally {
        ostream.flush()
        ostream.close()
      }
    }
    JsObject()
  }

  override protected def convertQuery(query: String): JsObject = {
    println(s"UNKNOWN ${request.host} // Query Raw")
    println(query)
    JsObject()
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation = QUERY): Unit = {
    println(s"UNKNOWN ${request.host} // Query Parsed")
    println(queryValues.prettyPrint)
  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation = BODY): Unit = {}

  override protected def parseHeader(
      implicit headerValues: JsObject,
      PIILocation: PIILocation = HEADER): Unit = {
    println(s"UNKNOWN ${request.host} // Header Parsed")
    println(headerValues.prettyPrint)
  }
}
