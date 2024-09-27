package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.facebook

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.EndpointParser.{
  EndpointParser,
  HeaderParser
}
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

object FacebookEndpoints extends EndpointParser {

  private val KNOWN_HOSTS = Set(
    "graph.facebook.com",
    "www.facebook.com",
    "web.facebook.com",
    "scontent-dus1-1.xx.fbcdn.net",
  )

  override def matches(request: Request): Boolean = {
    KNOWN_HOSTS.contains(request.host)
  }

  override def deploy(request: Request): List[PII] = {
    request.host match {
      case "graph.facebook.com" =>
        new GraphFacebook(request).getPii
      case "www.facebook.com" =>
        new WWWFacebook(request).getPii
      case "web.facebook.com" =>
        new WWWFacebook(request).getPii
      case "scontent-dus1-1.xx.fbcdn.net" =>
        new HeaderParser(request).getPii
    }
  }

}
