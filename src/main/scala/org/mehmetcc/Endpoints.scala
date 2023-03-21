package org.mehmetcc

import com.mongodb.MongoCommandException
import org.mehmetcc.PostAccelerationOnceRequest._
import org.mehmetcc.PostAccelerationResponse._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.Task

import scala.language.postfixOps

object Endpoints {
  val postAccelerationOnceServerEndpoint: ZServerEndpoint[Any, Any] =
    endpoint.post
      .in("acceleration")
      .in(jsonBody[PostAccelerationOnceRequest])
      .errorOut(statusCode)
      .errorOut(jsonBody[ErrorMessage])
      .out(jsonBody[PostAccelerationResponse])
      .serverLogic { request =>
        Database
          .insertOne(
            Acceleration(
              x = request.x,
              y = request.y,
              z = request.z,
              latitude = request.latitude,
              longitude = request.longitude
            )
          )
          .map(PostAccelerationResponse(_))
          .either
          .map(either =>
            either.left.map {
              case mongo: MongoCommandException => (StatusCode.Unauthorized, ErrorMessage(mongo.getMessage))
              case other: Exception             => (StatusCode.InternalServerError, ErrorMessage(other.getMessage))
            }
          )
          .provide(Configuration.live, Database.live)
      }

  val postAccelerationManyServerEndpoint: ZServerEndpoint[Any, Any] =
    endpoint.post
      .in("acceleration" / "batch")
      .in(jsonBody[PostAccelerationManyRequest])
      .errorOut(statusCode)
      .errorOut(jsonBody[ErrorMessage])
      .out(jsonBody[PostAccelerationResponse])
      .serverLogic { request =>
        Database
          .insertMany(
            request.accelerations.map(request =>
              Acceleration(
                x = request.x,
                y = request.y,
                z = request.z,
                latitude = request.latitude,
                longitude = request.longitude
              )
            )
          )
          .map(PostAccelerationResponse(_))
          .either
          .map(either =>
            either.left.map {
              case mongo: MongoCommandException => (StatusCode.Unauthorized, ErrorMessage(mongo.getMessage))
              case other: Exception             => (StatusCode.InternalServerError, ErrorMessage(other.getMessage))
            }
          )
          .provide(Configuration.live, Database.live)
      }

  val apiEndpoints: List[ZServerEndpoint[Any, Any]] =
    List(postAccelerationOnceServerEndpoint, postAccelerationManyServerEndpoint)

  val docEndpoints: List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "antiquities", "0.0.1-SNAPSHOT")

  val prometheusMetrics: PrometheusMetrics[Task] = PrometheusMetrics.default[Task]()
  val metricsEndpoint: ZServerEndpoint[Any, Any] = prometheusMetrics.metricsEndpoint

  val all: List[ZServerEndpoint[Any, Any]] = apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)
}
