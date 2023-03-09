package org.mehmetcc

import mongo4cats.bson.{BsonValue, Document, ObjectId}
import mongo4cats.zio.{ZMongoClient, ZMongoCollection, ZMongoDatabase}
import zio.{Task, ZIO, ZLayer}

import java.time.Instant

case class Acceleration(x: Double, y: Double, z: Double, receivedAt: Instant = Instant.now) {
  lazy val document: Document = Document(
    "_id"         -> BsonValue.objectId(ObjectId.gen),
    "x"           -> BsonValue.double(x),
    "y"           -> BsonValue.double(y),
    "z"           -> BsonValue.double(z),
    "received_at" -> BsonValue.instant(receivedAt)
  )
}

trait Database {
  def insertOne(acceleration: Acceleration): Task[Boolean]

  def insertMany(accelerations: List[Acceleration]): Task[Boolean]
}

object Database {
  val client: ZLayer[Configuration, Throwable, ZMongoClient] = ZLayer.scoped {
    for {
      configuration <- Configuration.load
      host           = configuration.databaseConfiguration.host
      port           = configuration.databaseConfiguration.port
      username       = configuration.databaseConfiguration.username
      password       = configuration.databaseConfiguration.password
      client        <- ZMongoClient.fromConnectionString(s"mongodb://${username}:${password}@${host}:${port}")
    } yield client
  }

  val database = ZLayer.fromZIO { // assign type annotation if you want to see some black magic fuckery
    for {
      configuration <- Configuration.load
      databaseName   = configuration.databaseConfiguration.databaseName
      client        <- ZIO.service[ZMongoClient]
      database      <- client.getDatabase(databaseName)
    } yield database
  }

  val live = Configuration.live >>> client >>> database >>> ZLayer {
    for {
      database   <- ZIO.service[ZMongoDatabase]
      collection <- database.getCollection("acceleration")
    } yield DatabaseLive(collection)
  }

  def insertOne(acceleration: Acceleration): ZIO[Database, Throwable, Boolean] =
    ZIO.serviceWithZIO[Database](_.insertOne(acceleration))

  def insertMany(accelerations: List[Acceleration]): ZIO[Database, Throwable, Boolean] =
    ZIO.serviceWithZIO[Database](_.insertMany(accelerations))
}

case class DatabaseLive(collection: ZMongoCollection[Document]) extends Database {
  override def insertOne(acceleration: Acceleration): Task[Boolean] =
    collection.insertOne(acceleration.document).map(_.wasAcknowledged())

  override def insertMany(accelerations: List[Acceleration]): Task[Boolean] =
    collection.insertMany(accelerations.map(_.document)).map(_.wasAcknowledged())
}
