package com.uridb.shorturl.repository

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoCollection, MongoDB}
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.typesafe.scalalogging.LazyLogging
import com.uridb.shorturl.model.URLInfo

/*
<code>
db.counter.insert(
   {
      _id: "urlId",
      seq: NumberLong("1080772224")
   }
)
</code>
 */
trait CounterRepositoryComponent {
   protected[this] val database: MongoDB
   val counterRepository: CounterRepository

   trait CounterRepository {
     def findCounter(name: String): Option[Long]
   }

   class CounterRepositoryImpl extends CounterRepository with LazyLogging {
     private[this] lazy val counterCollection = database("counter")

     def getCounterCollection(): MongoCollection = {
       counterCollection
     }

     override def findCounter(name: String): Option[Long] = {
       val queryResult = counterCollection.findAndModify(MongoDBObject("_id" -> name),null, null, false,$inc("seq" -> 1),true, true)
       queryResult match {
         case Some(dbo) => Some(dbo.getAs[Long]("seq").get)
         case None => None
       }
     }


   }

 }
