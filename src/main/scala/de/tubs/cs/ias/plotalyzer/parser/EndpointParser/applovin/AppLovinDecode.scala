package de.tubs.cs.ias.plotalyzer.parser.EndpointParser.applovin

import wvlet.log.LogSupport
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

object AppLovinDecode extends LogSupport {

  // enter your SDK key here
  val ANDROID_SDK_KEY =
    "_9CCiT5dTrkeiwnJpp7aiGRVIat6snJIBA09xWqdltXJMS41TbJub7IFq_TkdBe803MzRDM17JOAtgTqpvMvcJ"

  // enter your IV key here for this you need to analyze the SDK you are using
  val ANDROID_IV: Array[Byte] = Array(-83, -98, -53, -112, -29, -118, 55, 117,
    59, 8, -12, -15, 73, 110, -67, 57, 117, 4, -26, 97, 66, -12, 125, 91, -119,
    -103, -30, 114, 123, 54, 51, -77)

  def undoBase64Obfus(str: String): String = {
    str.replace('-', '+').replace('_', '/').replace('*', '=')
  }

  def obfusBase64(byteA: Array[Byte]): String = {
    new String(java.util.Base64.getEncoder.encode(byteA), "UTF-8")
      .replace('+', '-')
      .replace('/', '_')
      .replace('=', '*')
  }

  def convertBytesToHex(bytes: Seq[Byte]): String = {
    val sb = new StringBuilder
    for (b <- bytes) {
      sb.append(String.format("%02x", Byte.box(b)))
    }
    sb.toString
  }

  def digestIV(iv: Array[Byte]): String = {
    val digester: MessageDigest = MessageDigest.getInstance("SHA-1")
    digester.update(iv)
    convertBytesToHex(digester.digest())
  }

  def digestStringWithIv(str: String, iv: Array[Byte]): Array[Byte] = {
    val digester: MessageDigest = MessageDigest.getInstance("SHA-256")
    digester.update(iv)
    digester.update(str.getBytes("UTF-8"))
    val ret = digester.digest()
    ret
  }

  def rtEncode(str: String,
               j: Long,
               sdkKey: String,
               iv: Array[Byte]): String = {
    val substring: String = sdkKey.substring(32)
    val substring2: String = sdkKey.substring(0, 32)
    val bytes: Array[Byte] = str.getBytes("UTF-8")
    val a2: Array[Byte] = digestStringWithIv(substring2, iv)
    val byteArrayOutputStream = new ByteArrayOutputStream()
    byteArrayOutputStream.write(((j >> 0) & 255).asInstanceOf[Byte] ^ a2(0))
    byteArrayOutputStream.write(((j >> 8) & 255).asInstanceOf[Byte] ^ a2(1))
    byteArrayOutputStream.write(((j >> 16) & 255).asInstanceOf[Byte] ^ a2(2))
    byteArrayOutputStream.write(((j >> 24) & 255).asInstanceOf[Byte] ^ a2(3))
    byteArrayOutputStream.write(((j >> 32) & 255).asInstanceOf[Byte] ^ a2(4))
    byteArrayOutputStream.write(((j >> 40) & 255).asInstanceOf[Byte] ^ a2(5))
    byteArrayOutputStream.write(((j >> 48) & 255).asInstanceOf[Byte] ^ a2(6))
    byteArrayOutputStream.write(((j >> 56) & 255).asInstanceOf[Byte] ^ a2(7))
    var i = 0
    while (i < bytes.length) {
      val j2 = j + i
      val j3 = (j2 ^ (j2 >> 33)) * (-4417276706812531889L)
      val j4 = (j3 ^ (j3 >> 29)) * (-8796714831421723037L)
      val j5 = j4 ^ (j4 >> 32)
      byteArrayOutputStream.write(
        (((if (i + 0 >= bytes.length) 0.toByte
           else bytes(i + 0)) ^ a2((i + 0) % a2.length)) ^ ((j5 >> 0) & 255))
          .asInstanceOf[Byte])
      byteArrayOutputStream.write(
        (((if (i + 1 >= bytes.length) 0.toByte
           else bytes(i + 1)) ^ a2((i + 1) % a2.length)) ^ ((j5 >> 8) & 255))
          .asInstanceOf[Byte])
      byteArrayOutputStream.write(
        (((if (i + 2 >= bytes.length) 0.toByte
           else bytes(i + 2)) ^ a2((i + 2) % a2.length)) ^ ((j5 >> 16) & 255))
          .asInstanceOf[Byte])
      byteArrayOutputStream.write(
        (((if (i + 3 >= bytes.length) 0.toByte
           else bytes(i + 3)) ^ a2((i + 3) % a2.length)) ^ ((j5 >> 24) & 255))
          .asInstanceOf[Byte])
      byteArrayOutputStream.write(
        (((if (i + 4 >= bytes.length) 0.toByte
           else bytes(i + 4)) ^ a2((i + 4) % a2.length)) ^ ((j5 >> 32) & 255))
          .asInstanceOf[Byte])
      byteArrayOutputStream.write(
        (((if (i + 5 >= bytes.length) 0.toByte
           else bytes(i + 5)) ^ a2((i + 5) % a2.length)) ^ ((j5 >> 40) & 255))
          .asInstanceOf[Byte])
      byteArrayOutputStream.write(
        (((if (i + 6 >= bytes.length) 0.toByte
           else bytes(i + 6)) ^ a2((i + 6) % a2.length)) ^ ((j5 >> 48) & 255))
          .asInstanceOf[Byte])
      byteArrayOutputStream.write(
        (((if (i + 7 >= bytes.length) 0.toByte
           else bytes(i + 7)) ^ a2((i + 7) % a2.length)) ^ ((j5 >> 56) & 255))
          .asInstanceOf[Byte])

      i += 8
    }
    ("1:" + digestIV(iv) + ":" + substring + ":" + obfusBase64(
      byteArrayOutputStream.toByteArray)) //.getBytes("UTF-8")
  }

  case class DecodeException(message: String) extends Exception

  def rtDecode(str: String, str2: String, iv: Array[Byte]): String = {
    val split: Array[String] = str.split(':')
    if (split(0) != "1" || split.length != 4) {
      error(s"split(0): ${split(0)}")
      error(s"split length: ${split.length}")
      throw DecodeException(s"Wrong str format!")
    } else {
      val str3: String = split(1)
      val str4: String = split(2)
      val a2: Array[Byte] = Base64.getDecoder.decode(undoBase64Obfus(split(3)))
      if (str2.endsWith(str4) && digestIV(iv) == str3) {
        val a3: Array[Byte] = digestStringWithIv(str2.substring(0, 32), iv)
        val byteArrayInputStream: ByteArrayInputStream =
          new ByteArrayInputStream(a2)
        val r0: Long = (((byteArrayInputStream.read() ^ a3(0)) & 255L) << 0)
        val r1: Long = (((byteArrayInputStream.read() ^ a3(1)) & 255L) << 8)
        val r2: Long = (((byteArrayInputStream.read() ^ a3(2)) & 255L) << 16)
        val r3: Long = (((byteArrayInputStream.read() ^ a3(3)) & 255L) << 24)
        val r4: Long = (((byteArrayInputStream.read() ^ a3(4)) & 255L) << 32)
        val r5: Long = (((byteArrayInputStream.read() ^ a3(5)) & 255L) << 40)
        val r6: Long = (((byteArrayInputStream.read() ^ a3(6)) & 255L) << 48)
        val r7: Long = (((byteArrayInputStream.read() ^ a3(7)) & 255L) << 56)
        val read: Long = r0 | r1 | r2 | r3 | r4 | r5 | r6 | r7
        val byteArrayOutputStream: ByteArrayOutputStream =
          new ByteArrayOutputStream()
        var i: Int = 0
        val bArr = new Array[Byte](8)
        var read2 = byteArrayInputStream.read(bArr)
        while (read2 >= 0) {
          val j: Long = read + i
          val j2: Long = (j ^ (j >> 33)) * (-4417276706812531889L)
          val j3: Long = (j2 ^ (j2 >> 29)) * (-8796714831421723037L)
          val j4: Long = j3 ^ (j3 >> 32)
          byteArrayOutputStream.write(
            ((bArr(0) ^ a3((i + 0) % a3.length)) ^ ((j4 >> 0) & 255))
              .asInstanceOf[Byte])
          byteArrayOutputStream.write(
            ((bArr(1) ^ a3((i + 1) % a3.length)) ^ ((j4 >> 8) & 255))
              .asInstanceOf[Byte])
          byteArrayOutputStream.write(
            ((bArr(2) ^ a3((i + 2) % a3.length)) ^ ((j4 >> 16) & 255))
              .asInstanceOf[Byte])
          byteArrayOutputStream.write(
            ((bArr(3) ^ a3((i + 3) % a3.length)) ^ ((j4 >> 24) & 255))
              .asInstanceOf[Byte])
          byteArrayOutputStream.write(
            ((bArr(4) ^ a3((i + 4) % a3.length)) ^ ((j4 >> 32) & 255))
              .asInstanceOf[Byte])
          byteArrayOutputStream.write(
            ((bArr(5) ^ a3((i + 5) % a3.length)) ^ ((j4 >> 40) & 255))
              .asInstanceOf[Byte])
          byteArrayOutputStream.write(
            ((bArr(6) ^ a3((i + 6) % a3.length)) ^ ((j4 >> 48) & 255))
              .asInstanceOf[Byte])
          byteArrayOutputStream.write(
            ((bArr(7) ^ a3((i + 7) % a3.length)) ^ ((j4 >> 56) & 255))
              .asInstanceOf[Byte])
          read2 = byteArrayInputStream.read(bArr)
          i += 8
        }
        new String(byteArrayOutputStream.toByteArray, StandardCharsets.UTF_8).trim
      } else {
        throw DecodeException(s"Wrong IV")
      }
    }
  }

}
