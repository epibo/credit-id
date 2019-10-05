package com.creditid.cid.web.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, onSuccess, path, post}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.creditid.cid.web.models._

import scala.concurrent.Future

private[routes] final class Validation(implicit system: ActorSystem, operations:ActorRef) {
//  4. 凭证使用
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import akka.pattern.ask
  import scala.concurrent.duration._
  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler = system.scheduler


  def validate: Route = post {
    path("cert") {
      entity(as[Validate]) { request =>
        val operationPerformed: Future[Either[Throwable, ValidateResult]] = (operations ? request).mapTo[Either[Throwable, ValidateResult]]
        onSuccess(operationPerformed) {
          case Right(value) => complete(StatusCodes.OK, value)
          case Left(reason) => complete(StatusCodes.InternalServerError -> reason)
        }
      }
    }
  }
//  （1）验证凭证时，向链查验凭证状态（根据输入的链上ID，返回凭证的有效状态：有效、不存在、已失效。并同时返回凭证的明文）
//  in：凭证的链上ID，凭证提供机构签名（对随机数签名，随机数由链这边生成，后提供给公共服务平台）
//  out：凭证状态、凭证指纹、凭证明文

}
