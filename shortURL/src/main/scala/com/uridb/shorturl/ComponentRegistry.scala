package com.uridb.shorturl

import com.uridb.shorturl.cache.URLInfoCacheComponent
import com.uridb.shorturl.repository.{CounterRepositoryComponent, URLInfoRepositoryComponent}
import com.uridb.shorturl.service.URLInfoServiceComponent


object ComponentRegistry extends URLInfoServiceComponent with URLInfoRepositoryComponent with CounterRepositoryComponent with URLInfoCacheComponent{
  override val urlInfoService: URLInfoService = new URLInfoServiceImpl()
  override val urlInfoRepository:URLInfoRepository = new URLInfoRepositoryImpl()
  override val counterRepository: CounterRepository = new CounterRepositoryImpl()
  override val urlInfoCache: ComponentRegistry.URLInfoCache = if (Config.enableCache) new URLInfoCacheImpl() else new URLInfoNoCacheImpl()

  override protected[this] val database = Config.mongoDatabase

}
