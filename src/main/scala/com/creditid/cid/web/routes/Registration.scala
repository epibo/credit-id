package com.creditid.cid.web.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, onSuccess, path, post}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.creditid.cid.web.models.Register

import scala.concurrent.Future

private[routes] final class Registration(implicit system: ActorSystem, operations:ActorRef) {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  //
  //  1. 注册流程
  //  （1）CID注册上链（CID的标识上链）
  //  in：CID、个人信息字符串
  //  out:状态码。200成功
  import akka.pattern.ask
  import scala.concurrent.duration._
  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler = system.scheduler
  def register: Route = path("register") {
    post {
      entity(as[Register]) { request =>
        val operationPerformed: Future[Either[Throwable, Int]] = (operations ? request).mapTo[Either[Throwable, Int]]
        onSuccess(operationPerformed) {
          case Right(value) => if (value > 0) complete(StatusCodes.OK) else complete(StatusCodes.AlreadyReported)
          case Left(reason) => complete(StatusCodes.InternalServerError -> reason)
        }
      }
    }
  }
}
