package com.uridb.shorturl.model

import com.novus.salat.annotations.Key
import org.joda.time.DateTime

case class URLInfo(urlId: Long, url: String, @Key("_id") id: Option[String] = None,timestamp: DateTime = new DateTime) {

}
