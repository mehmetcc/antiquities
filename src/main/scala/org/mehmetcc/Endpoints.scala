package org.mehmetcc

import org.mehmetcc.Acceleration._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{Task, ZIO}

object Endpoints {
  val postAccelerationEndpoint: Endpoint[Unit, Acceleration, (StatusCode, ErrorMessage), Acceleration, Any] =
    endpoint.post
      .in("acceleration")
      .in(jsonBody[Acceleration])
      .errorOut(statusCode)
      .errorOut(jsonBody[ErrorMessage])
      .out(jsonBody[Acceleration])
  val postAccelerationServerEndpoint: ZServerEndpoint[Any, Any] =
    postAccelerationEndpoint.serverLogicSuccess(request => ZIO.succeed(Acceleration(request.x, request.y, request.z)))

  val apiEndpoints: List[ZServerEndpoint[Any, Any]] = List(postAccelerationServerEndpoint)

  val docEndpoints: List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "antiquities", "1.0.0")

  val prometheusMetrics: PrometheusMetrics[Task] = PrometheusMetrics.default[Task]()
  val metricsEndpoint: ZServerEndpoint[Any, Any] = prometheusMetrics.metricsEndpoint

  val all: List[ZServerEndpoint[Any, Any]] = apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)
}
