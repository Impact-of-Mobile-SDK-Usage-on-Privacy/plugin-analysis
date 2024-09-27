package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.flurry

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.EndpointParser
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

object FlurryEndpoints extends EndpointParser {

  private val KNOWN_HOSTS = Set(
    "data.flurry.com",
  )

  override def matches(request: Request): Boolean = {
    KNOWN_HOSTS.contains(request.host)
  }

  override def deploy(request: Request): List[PII] = {
    request.host match {
      case "data.flurry.com" => new FlurryData(request).getPii
    }
  }

}
