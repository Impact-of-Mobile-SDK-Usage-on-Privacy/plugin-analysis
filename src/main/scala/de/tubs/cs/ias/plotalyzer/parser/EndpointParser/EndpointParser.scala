package de.tubs.cs.ias.plotalyzer.parser.EndpointParser

import de.halcony.plotalyzer.database.entities.trafficcollection.Request
import de.tubs.cs.ias.plotalyzer.parser.pii.PII

trait EndpointParser {

  def deploy(request: Request): List[PII]

  def matches(request: Request): Boolean

}
