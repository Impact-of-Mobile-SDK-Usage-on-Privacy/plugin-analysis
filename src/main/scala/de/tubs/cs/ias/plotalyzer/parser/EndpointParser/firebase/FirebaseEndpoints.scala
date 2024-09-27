package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.firebase

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.EndpointParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

object FirebaseEndpoints extends EndpointParser {

  private val KNOWN_HOSTS = Set(
    "region1.app-measurement.com",
    "firebaseinstallations.googleapis.com",
    "app-measurement.com"
  )

  override def matches(request: Request): Boolean = {
    KNOWN_HOSTS.contains(request.host)
  }

  override def deploy(request: Request): List[PII] = {
    request.host match {
      case "firebaseinstallations.googleapis.com" =>
        new FirebaseInstallation(request).getPii
      case "region1.app-measurement.com" | "app-measurement.com" =>
        new FirebaseAppMeasurement(request).getPii
    }
  }

}
