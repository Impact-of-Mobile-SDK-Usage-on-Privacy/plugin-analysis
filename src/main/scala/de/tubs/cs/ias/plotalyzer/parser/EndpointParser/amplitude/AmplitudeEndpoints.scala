package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.amplitude

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.EndpointParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

object AmplitudeEndpoints extends EndpointParser {

  private val KNOWN_HOSTS = Set[String](
    "api2.amplitude.com",
    "api.eu.amplitude.com"
  )

  override def matches(request: Request): Boolean = {
    KNOWN_HOSTS.contains(request.host)
  }

  override def deploy(request: Request): List[PII] = {
    request.host match {
      case "api2.amplitude.com"   => new ApiAmplitude(request).getPii
      case "api.eu.amplitude.com" => new ApiEuAmplitude(request).getPii
    }
  }

}
