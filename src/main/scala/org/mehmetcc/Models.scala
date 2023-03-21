package org.mehmetcc

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class PostAccelerationOnceRequest(x: Double, y: Double, z: Double, longitude: Double, latitude: Double)

object PostAccelerationOnceRequest {
  implicit val postAccelerationOnceRequestEncoder: JsonEncoder[PostAccelerationOnceRequest] =
    DeriveJsonEncoder.gen[PostAccelerationOnceRequest]

  implicit val postAccelerationOnceRequestEncoderDecoder: JsonDecoder[PostAccelerationOnceRequest] =
    DeriveJsonDecoder.gen[PostAccelerationOnceRequest]
}

case class PostAccelerationManyRequest(accelerations: List[PostAccelerationOnceRequest])

object PostAccelerationManyRequest {
  implicit val postAccelerationManyRequestEncoder: JsonEncoder[PostAccelerationManyRequest] =
    DeriveJsonEncoder.gen[PostAccelerationManyRequest]

  implicit val postAccelerationManyRequestDecoder: JsonDecoder[PostAccelerationManyRequest] =
    DeriveJsonDecoder.gen[PostAccelerationManyRequest]
}

case class PostAccelerationResponse(isSuccess: Boolean)

object PostAccelerationResponse {
  implicit val postAccelerationResponseEncoder: JsonEncoder[PostAccelerationResponse] =
    DeriveJsonEncoder.gen[PostAccelerationResponse]

  implicit val postAccelerationResponseDecoder: JsonDecoder[PostAccelerationResponse] =
    DeriveJsonDecoder.gen[PostAccelerationResponse]
}

case class ErrorMessage(message: String)

object ErrorMessage {
  implicit val errorMessageEncoder: JsonEncoder[ErrorMessage] = DeriveJsonEncoder.gen[ErrorMessage]

  implicit val errorMessafeDecoder: JsonDecoder[ErrorMessage] = DeriveJsonDecoder.gen[ErrorMessage]
}
