package org.mehmetcc

import zio.config._
import zio.config.magnolia._
import zio.config.typesafe._
import zio.{IO, Task, ULayer, ZIO, ZLayer}

final case class ApplicationConfiguration(
  httpConfiguration: HttpConfiguration,
  databaseConfiguration: DatabaseConfiguration
)

final case class HttpConfiguration(port: Int)

final case class DatabaseConfiguration(
  databaseName: String,
  host: String,
  port: Int,
  username: String,
  password: String
)

trait Configuration {
  def load: IO[ReadError[String], ApplicationConfiguration]
}

object Configuration {
  val live: ULayer[Configuration] = ZLayer.succeed(new Configuration {
    override def load: IO[ReadError[String], ApplicationConfiguration] = read {
      descriptor[ApplicationConfiguration].mapKey(toKebabCase).from(TypesafeConfigSource.fromResourcePath)
    }
  })

  def load: ZIO[Configuration, ReadError[String], ApplicationConfiguration] = ZIO.serviceWithZIO[Configuration](_.load)
}
