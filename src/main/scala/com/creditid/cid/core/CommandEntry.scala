package com.creditid.cid.core

import cats.effect.{ConcurrentEffect, ContextShift, Effect, Timer}
import fs2.Stream
import cats.implicits._
import enumeratum._
import monix.execution.Scheduler
import org.backuity.clist._
import com.creditid.cid.operations._
import com.creditid.cid.utils.configs.HttpConfig
import com.creditid.cid.web.routes
import com.creditid.cid.web.routes.Routers
import org.http4s.blaze.http.HttpService
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.middleware.{CORS, GZip, Logger}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.HttpMiddleware

import scala.concurrent.ExecutionContext
import pureconfig._
import pureconfig.generic.auto._

sealed abstract class CommandEntry(name: String, description: String)
  extends Command(name, description)
    with EnumEntry {
  var help: Boolean = opt[Boolean](default = false, description = "display list of available commands")
}

object CommandEntry extends Enum[CommandEntry] {
  override val values = findValues

  case object Run extends CommandEntry("run", "start application") {
    val config: HttpConfig = ConfigSource.default.load[HttpConfig].getOrElse(HttpConfig("0.0.0.0", 8080))
  //  class HttpServer[F[_]: Effect] extends StreamApp[F] {


    def stream[F[_] : ConcurrentEffect](scheduler: Scheduler)(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

      for {
        client <- BlazeClientBuilder[F](ExecutionContext.fromExecutor(scheduler)).stream
        (cert, reg, sign, valid) = (Certification[F], Registration[F], SignOff[F], Validation[F])
        ops = new routes.Routers[F](Http4sDsl[F])
        services = ops.records(cert) <+> ops.register(reg) <+> ops.signoffs(sign) <+> ops.validation(valid)

        finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(CORS(GZip(services orNotFound)))
        exitCode <- BlazeServerBuilder[F]
          .bindHttp(config.port, config.host)
          // .withWebSockets()
          .withHttpApp(finalHttpApp)
          .serve
      } yield exitCode
      }.drain
  }

}
