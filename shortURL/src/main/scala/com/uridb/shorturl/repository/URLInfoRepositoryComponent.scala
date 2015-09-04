package com.uridb.shorturl.repository

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.mongodb.casbah.{MongoCollection, MongoDB}
import com.novus.salat._
import com.novus.salat.global._
import com.typesafe.scalalogging.LazyLogging
import com.uridb.shorturl.model.URLInfo

trait URLInfoRepositoryComponent {
  protected[this] val database: MongoDB
  val urlInfoRepository: URLInfoRepository

  trait URLInfoRepository {
    def findByUrlId(urlId: Long): Option[URLInfo]
    def findByUrl(url: String): Option[URLInfo]
    def save(urlInfo: URLInfo): Option[URLInfo]
  }

  class URLInfoRepositoryImpl extends URLInfoRepository with LazyLogging {
    RegisterJodaTimeConversionHelpers()
    private[this] lazy val urlInfoCollection = database("urlinfo")

    def getURLInfoCollection(): MongoCollection = {
      urlInfoCollection
    }

    override def findByUrlId(urlId: Long): Option[URLInfo] = {
      urlInfoCollection.findOne(MongoDBObject("urlId" -> urlId)) match {
        case Some(dbo) => Some(grater[URLInfo].asObject(dbo))
        case None => None
      }
    }

    override def findByUrl(url: String): Option[URLInfo] = {
      urlInfoCollection.findOne(MongoDBObject("url" -> url)) match {
        case Some(dbo) => Some(grater[URLInfo].asObject(dbo))
        case None => None
      }
    }

    override def save(urlInfo: URLInfo): Option[URLInfo] = {
      val dbo = grater[URLInfo].asDBObject(urlInfo)
      urlInfoCollection.save(dbo)
      findByUrlId(urlInfo.urlId)
    }


  }

}
