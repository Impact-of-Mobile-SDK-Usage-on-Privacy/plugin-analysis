package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.util

import com.google.protobuf.{ByteString, InvalidProtocolBufferException}
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsValue}

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MMap}
import scala.jdk.CollectionConverters.{CollectionHasAsScala, MapHasAsScala}

object ProtobufToJson {

  def createJsonObject(
      fieldSet: com.google.protobuf.UnknownFieldSet): JsObject = {
    val retMap: MMap[Int, ListBuffer[JsValue]] = MMap()

    def addOrCreate(number: Int, element: JsValue): Unit = {
      retMap.get(number) match {
        case Some(value) => value.addOne(element)
        case None        => retMap.addOne(number -> ListBuffer(element))
      }
    }

    def addVarInt(number: Int, list: List[java.lang.Long]): Unit = {
      list.foreach(elem => addOrCreate(number, JsNumber(elem)))
    }

    def addFixed32(number: Int, list: List[java.lang.Integer]): Unit = {
      list.foreach(elem =>
        addOrCreate(number, JsString(s"0x%08x".format(elem))))
    }

    def addFixed64(number: Int, list: List[java.lang.Long]): Unit = {
      list.foreach(elem =>
        addOrCreate(number, JsString(s"0x%016x".format(elem))))
    }

    def addVariableLength(number: Int, list: List[ByteString]): Unit = {
      list.foreach { elem =>
        try {
          val message = com.google.protobuf.UnknownFieldSet.parseFrom(elem)
          addOrCreate(number, createJsonObject(message))
        } catch {
          case _: InvalidProtocolBufferException =>
            addOrCreate(
              number,
              JsString(com.google.protobuf.TextFormat.escapeBytes(elem)))
        }
      }
    }

    def addUnknownFieldSet(
        number: Int,
        fieldSet: com.google.protobuf.UnknownFieldSet): Unit = {
      addOrCreate(number, createJsonObject(fieldSet))
    }

    fieldSet.asMap().asScala.foreach {
      case (number: Integer,
            value: com.google.protobuf.UnknownFieldSet.Field) =>
        addVarInt(number, value.getVarintList.asScala.toList)
        addFixed32(number, value.getFixed32List.asScala.toList)
        addFixed64(number, value.getFixed64List.asScala.toList)
        addVariableLength(number, value.getLengthDelimitedList.asScala.toList)
        value.getGroupList.forEach(gle => addUnknownFieldSet(number, gle))
    }
    JsObject(
      retMap.map(elem => elem._1.toString -> JsArray(elem._2.toVector)).toMap
    )
  }

}
