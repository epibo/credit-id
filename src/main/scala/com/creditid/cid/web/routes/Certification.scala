package com.creditid.cid.web.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.creditid.cid.web.models._

import scala.concurrent.Future

private[routes] final class Certification(implicit system: ActorSystem, operations:ActorRef) {
  //  2. 认证流程

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import akka.pattern.ask
  import scala.concurrent.duration._
  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler = system.scheduler
  //  （1）认证记录上链（认证操作，操作本身编码后上链）
  //  in：CID、记录
  //  out:状态码。200成功
  def records: Route = path("record") {
    post {
      entity(as[Details]) { request =>
        val operationPerformed: Future[Either[Throwable, Int]] = (operations ? request).mapTo[Either[Throwable, Int]]
        onSuccess(operationPerformed) {
          case Right(value) => if (value > 0) complete(StatusCodes.OK) else complete(StatusCodes.AlreadyReported)
          case Left(reason) => complete(StatusCodes.InternalServerError -> reason)
        }
      }
    }
  }

}
