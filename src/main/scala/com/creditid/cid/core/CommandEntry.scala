package com.creditid.cid.core

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import fs2.Stream
import cats.implicits._
import enumeratum._
import monix.execution.Scheduler
import org.backuity.clist._
import com.creditid.cid.operations._
import com.creditid.cid.utils.configs.HttpConfig
import com.creditid.cid.web.routes
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.middleware.GZip
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext
import pureconfig._
import pureconfig.generic.auto._

sealed abstract class CommandEntry(name: String, description: String)
  extends Command(name, description)
    with EnumEntry {
  var help = opt[Boolean](default = false, description = "display list of available commands")
}

object CommandEntry extends Enum[CommandEntry] {
  override val values = findValues

  case object Run extends CommandEntry("run", "start application") {
    val config: HttpConfig = ConfigSource.default.load[HttpConfig].getOrElse(HttpConfig("0.0.0.0",8080))
    def stream[F[_]: ConcurrentEffect](scheduler: Scheduler)(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
      for {
        client <- BlazeClientBuilder[F](ExecutionContext.fromExecutor(scheduler)).stream
        cert = Certification.impl[F]
        reg = Registration.impl[F]
        sign = SignOff.impl[F]
        valid = Validation.impl[F]
        ops = new routes.Routings(Http4sDsl.apply[F])
        services = GZip(
          ops.records(cert) <+> ops.register(reg) <+> ops.signoffs(sign) <+> ops.validation(valid)
        ).orNotFound

        finalHttpApp = Logger.httpApp(true, true)(services)

        exitCode <- BlazeServerBuilder[F]
          .bindHttp(config.port, config.host)
          .withHttpApp(finalHttpApp)
          .serve
      } yield exitCode
      }.drain
  }
}
