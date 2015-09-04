package com.uridb.shorturl


import com.mongodb.casbah.{MongoClientURI, MongoClient}
import com.typesafe.config.ConfigFactory
import shade.memcached.{Configuration, Memcached}
import scala.concurrent.ExecutionContext.Implicits.{global => ec}
import scala.concurrent.duration.Duration

object Config {
  val conf = ConfigFactory.load()
  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")
  val returnHost = conf.getString("server.returnHost")
  val enableCORS = conf.getBoolean("server.enableCORS")

  val mongoClient = MongoClient(MongoClientURI(conf.getString("server.mongo_connection_uri")))
  val mongoDatabase = mongoClient(conf.getString("server.mongo_database"))
  val enableCache = conf.getBoolean("server.enableCache")
  val memcached = if (enableCache) Memcached(Configuration(conf.getString("server.memcached_url")), ec) else null

  val memcachedDuration = Duration.fromNanos(conf.getDuration("server.cached_duration").toNanos)

  //implicit def asFiniteDuration(d: java.time.Duration) = scala.concurrent.duration.Duration.fromNanos(d.toNanos)
}
