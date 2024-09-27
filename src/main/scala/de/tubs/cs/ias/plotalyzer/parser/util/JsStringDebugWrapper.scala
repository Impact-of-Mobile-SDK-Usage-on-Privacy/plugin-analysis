package de.tubs.cs.ias.plotalyzer.parser.util

import spray.json.JsString

object JsStringDebugWrapper {

  case class BadJsStringValue() extends Throwable

  def create(string: String): JsString = {
    if (string == null) {
      throw BadJsStringValue()
    } else {
      JsString(string)
    }
  }

}
