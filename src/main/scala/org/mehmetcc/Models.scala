package org.mehmetcc

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDateTime

case class Acceleration(x: Double, y: Double, z: Double, receivedAt: LocalDateTime = LocalDateTime.now)

object Acceleration {
  implicit val accelerationEncoder: JsonEncoder[Acceleration] = DeriveJsonEncoder.gen[Acceleration]

  implicit val accelerationDecoder: JsonDecoder[Acceleration] = DeriveJsonDecoder.gen[Acceleration]
}

case class ErrorMessage(message: String)

object ErrorMessage {
  implicit val errorMessageEncoder: JsonEncoder[ErrorMessage] = DeriveJsonEncoder.gen[ErrorMessage]

  implicit val errorMessafeDecoder: JsonDecoder[ErrorMessage] = DeriveJsonDecoder.gen[ErrorMessage]
}
