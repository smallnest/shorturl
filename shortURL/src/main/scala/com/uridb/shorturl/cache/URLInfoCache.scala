package com.uridb.shorturl.cache

import com.typesafe.scalalogging.LazyLogging
import com.uridb.shorturl.Config
import shade.memcached.MemcachedCodecs._
import scala.concurrent.ExecutionContext.Implicits.{global => ec}

trait URLInfoCacheComponent {
  val urlInfoCache: URLInfoCache

  trait URLInfoCache {
    def setUrlIdAndUrl(urlId: Long, url: String): Unit
    def setUrlAndUrlId(url: String, urlId: Long): Unit
    def getUrlByUrlId(urlId: Long) : Option[String]
    def getUrlIdByUrl(url: String): Option[Long]
  }
  class URLInfoCacheImpl extends URLInfoCache with LazyLogging {
    def setUrlIdAndUrl(urlId: Long, url: String): Unit = {
      Config.memcached.set(urlId.toString, url, Config.memcachedDuration)(StringBinaryCodec)
    }

    def setUrlAndUrlId(url: String, urlId: Long): Unit = {
      Config.memcached.set(url, urlId, Config.memcachedDuration)(LongBinaryCodec)
    }

    def getUrlByUrlId(urlId: Long) = {
      Config.memcached.awaitGet[String](urlId.toString)(StringBinaryCodec)
    }

    def getUrlIdByUrl(url: String) = {
      Config.memcached.awaitGet[Long](url)(LongBinaryCodec)
    }
  }

  class URLInfoNoCacheImpl extends URLInfoCache with LazyLogging {
    override def setUrlIdAndUrl(urlId: Long, url: String): Unit = ()

    override def setUrlAndUrlId(url: String, urlId: Long): Unit = ()

    override def getUrlIdByUrl(url: String): Option[Long] = None

    override def getUrlByUrlId(urlId: Long): Option[String] = None
  }
}
