package com.creditid.cid.core

import cats.effect.{ConcurrentEffect, ContextShift, IO, Sync, Timer}
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

  case object Run extends CommandEntry("start", "start application...") {
    val config: HttpConfig = ConfigSource.default.load[HttpConfig].getOrElse(HttpConfig("0.0.0.0", 8080))
    var publicKey = opt[Option[String]](default = None)
    def stream[F[_] : Sync : ConcurrentEffect](scheduler: Scheduler)(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
      val service = client.ontService
      val account = service.accountOf(client.LABEL, client.PASSWORD)
      val ops = new routes.Routers[F](Http4sDsl[F], publicKey.map(_.toArray.map(_.toByte)))

      for {
        done <- ops.init(service, account)
        _ <- if (done) Sync[F].unit else Sync[F].raiseError[Unit](new IllegalStateException("【部署智能合约】或【初始化合约】失败，请稍后重试！"))
      } yield ()

      val (org, cid, credit, use) = (OrgOps(service, account), CidOps(service, account), CreditOps(service, account), CreditUse(service, account))
      val services = ops.orgOps(service, org) <+> ops.cigOps(service, cid) <+> ops.creditOps(service, credit) <+>
        ops.creditUse(service, use) <+> ops.creditUseAuth(service, use)
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
