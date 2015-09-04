package com.uridb.shorturl

object ShortURL {
  private val shortURLPattern = "[0-9a-zA-Z|\\+|~]{4,8}".r
  private val base64chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+~"
  private val charIndex = (for (i <- base64chars.indices) yield (base64chars(i),i)).toMap
  def encode(x:Long,sb: java.lang.StringBuilder = new java.lang.StringBuilder("")) = {
    var v = x
    while (v > 0) {
      sb append base64chars.charAt((v & 63).toInt)
      v = v >>> 6
    }
    sb.toString
  }

  def decode(url:String) = {
    if (url == null || url.isEmpty)
      0L

    var num = 0L
    val lastIndex = url.length - 1
    for (i <- url.indices)
      num = (num << 6) + charIndex(url(lastIndex - i))

    num
  }

  def isValid(shortURL:String) = {
    shortURL match {
      case shortURLPattern(_*) => true
      case _ => false
    }
  }
}
