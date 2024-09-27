package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.firebase

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.PIILocations.PIILocation
import de.tubs.cs.ias.plotalyzer.parser.PIIParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PiiTypes
import de.tubs.cs.ias.plotalyzer.parser.util.JsStringDebugWrapper
import spray.json.{JsObject, JsonParser}
import wvlet.log.LogSupport

class FirebaseInstallation(override val request: Request)
    extends PIIParser
    with LogSupport {

  override protected def convertBody(body: String,
                                     binary: Array[Byte]): JsObject = {
    if (body != null && body.nonEmpty) {
      JsonParser(body).asJsObject()
    } else if (binary != null && binary.nonEmpty) {
      warn(s"missing binary stuff of size ${binary.length}")
      JsObject()
    } else {
      JsObject()
    }
  }

  override protected def convertQuery(query: String): JsObject = {
    val json: JsObject = super.convertQuery(query)
    val splitPath: Array[String] = request.getPath.split('/')
    // the path contains the name of the fb project which can be linked to the app
    if (splitPath.length == 5 && splitPath(2) == "projects" && splitPath(4) == "installations") {
      JsObject(
        Map(
          "fb-project-name" -> JsStringDebugWrapper
            .create(splitPath.apply(3))) ++ json.fields
      )
    } else {
      json
    }
  }

  override protected def parseQuery(implicit queryValues: JsObject,
                                    PIILocation: PIILocation): Unit = {  }

  override protected def parseBody(implicit bodyValues: JsObject,
                                   PIILocation: PIILocation): Unit = {
    addPii(PiiTypes.APP_ID, "appId")
    addPii(PiiTypes.ID_OTHER, "fid")
  }

  override protected def parseHeader(implicit headerValues: JsObject,
                                     PIILocation: PIILocation): Unit = {
    super.parseHeader(headerValues, PIILocation)
    addPii(PiiTypes.APP_ID, "X-Android-Package")
  }

}
