package org.mehmetcc

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter.VertxFutureToRIO
import sttp.tapir.server.vertx.zio.{VertxZioServerInterpreter, VertxZioServerOptions}
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {
  override implicit val runtime: zio.Runtime[Any] = zio.Runtime.default

  val program: ZIO[Any, Throwable, HttpServer] = {
    val port   = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8080)
    val vertx  = Vertx.vertx()
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)

    for {
      serverStart <- ZIO.attempt {
                       val serverOptions = VertxZioServerOptions.customiseInterceptors
                         .metricsInterceptor(Endpoints.prometheusMetrics.metricsInterceptor())
                         .options
                       Endpoints.all.foreach { endpoint =>
                         VertxZioServerInterpreter(serverOptions)
                           .route(endpoint)
                           .apply(router)
                       }
                       server.requestHandler(router).listen(port)
                     }
                       .flatMap(_.asRIO)
      _ <- ZIO.log(s"Go to http://localhost:${serverStart.actualPort()}/docs to open SwaggerUI.")
      _ <- ZIO.unit.forever
    } yield serverStart
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = program.provide(Configuration.live).exitCode
}
