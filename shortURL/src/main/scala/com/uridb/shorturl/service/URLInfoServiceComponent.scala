package com.uridb.shorturl.service

import com.typesafe.scalalogging.LazyLogging
import com.uridb.shorturl.cache.URLInfoCacheComponent
import com.uridb.shorturl.model.URLInfo
import com.uridb.shorturl.repository.{CounterRepositoryComponent, URLInfoRepositoryComponent}

trait URLInfoServiceComponent {
  this: URLInfoRepositoryComponent with CounterRepositoryComponent with URLInfoCacheComponent =>
  val urlInfoService: URLInfoService

  trait URLInfoService {
    def findByUrlId(urlId: Long): Option[URLInfo]

    def findByUrl(url: String): Option[URLInfo]

    def save(urlInfo: URLInfo): Option[URLInfo]
  }

  class URLInfoServiceImpl extends URLInfoService with LazyLogging {
    override def findByUrlId(urlId: Long): Option[URLInfo] = {
      urlInfoCache.getUrlByUrlId(urlId) match {
        case Some(url) => Some(URLInfo(urlId, url))
        case None =>
          val urlInfo = urlInfoRepository.findByUrlId(urlId)
          if (urlInfo.nonEmpty) {
            urlInfoCache.setUrlAndUrlId(urlInfo.get.url,urlInfo.get.urlId)
            urlInfoCache.setUrlIdAndUrl(urlInfo.get.urlId, urlInfo.get.url)
          }
          urlInfo
      }

    }

    override def findByUrl(url: String): Option[URLInfo] = {
      //metrics
      urlInfoCache.getUrlIdByUrl(url) match {
        case Some(urlId) => Some(URLInfo(urlId, url))
        case None =>
          val urlInfo = urlInfoRepository.findByUrl(url)
          if (urlInfo.nonEmpty) {
            urlInfoCache.setUrlAndUrlId(urlInfo.get.url,urlInfo.get.urlId)
            urlInfoCache.setUrlIdAndUrl(urlInfo.get.urlId, urlInfo.get.url)
          }
          urlInfo
      }
    }

    override def save(urlInfo: URLInfo): Option[URLInfo] = {
      //metrics
      if (urlInfo.urlId == 0)
        checkAndSave(urlInfo.copy(urlId = counterRepository.findCounter("urlId").get))
      else
        checkAndSave(urlInfo)
    }

    def checkAndSave(urlInfo: URLInfo) = {
      findByUrl(urlInfo.url) match {
        case Some(existedUrlInfo) => Some(existedUrlInfo)
        case None =>
          val savedUrlInfo = urlInfoRepository.save(urlInfo)
          if (savedUrlInfo.nonEmpty) {
            urlInfoCache.setUrlAndUrlId(savedUrlInfo.get.url,savedUrlInfo.get.urlId)
            urlInfoCache.setUrlIdAndUrl(savedUrlInfo.get.urlId, savedUrlInfo.get.url)
          }
          savedUrlInfo
      }

    }

  }

}
