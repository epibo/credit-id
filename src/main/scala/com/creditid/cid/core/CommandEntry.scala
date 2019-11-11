package com.creditid.cid.core

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.creditid.cid.client
import com.creditid.cid.operations._
import com.creditid.cid.utils.configs.HttpConfig
import com.creditid.cid.web.routes
import enumeratum._
import fs2.Stream
import monix.execution.Scheduler
import org.backuity.clist._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, GZip, Logger}
import pureconfig.generic.auto._
import pureconfig._

sealed abstract class CommandEntry(name: String, description: String)
  extends Command(name, description)
    with EnumEntry {
  var help: Boolean = opt[Boolean](default = false, description = "display list of available commands")
}

object CommandEntry extends Enum[CommandEntry] {
  override val values = findValues

  case object Run extends CommandEntry("run", "start application") {
    val config: HttpConfig = ConfigSource.default.load[HttpConfig].getOrElse(HttpConfig("0.0.0.0", 8080))

    def stream[F[_] : ConcurrentEffect](scheduler: Scheduler)(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
      val ontService = client.ontService
      val (org, cid, credit, use) = (OrgOps(ontService), CidOps(ontService), CreditOps(ontService), CreditUse(ontService))
      val ops = new routes.Routers[F](Http4sDsl[F])
      val services = ops.orgOps(org) <+> ops.cigOps(cid) <+> ops.creditOps(credit) <+> ops.creditUse(use)
      val finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(CORS(GZip(services orNotFound)))

      for {
        exitCode <- BlazeServerBuilder[F]
          .bindHttp(config.port, config.host)
          // .withWebSockets()
          .withHttpApp(finalHttpApp)
          .serve
      } yield exitCode
      }.drain
  }

}
