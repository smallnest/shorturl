package com.uridb.shorturl

import java.net.URL

object URLUtil {
  def isValidURL(s:String) = { //check local adddress and deadloop
    try {
      if (s.startsWith("http://") || s.startsWith("https://")) { //only service for http and https
        val URL = new URL(s)
        URL.toURI
        true
      } else
        false
    } catch {
      case _:Throwable => false
    }
  }
}
