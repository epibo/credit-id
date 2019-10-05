package org.epibo.web.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import org.epibo.web.models._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import cats.implicits._

import scala.concurrent.Future

private[routes] final class SignOff(implicit system: ActorSystem, operations:ActorRef) {
  //  3. 凭证签发
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import akka.pattern.ask
  import scala.concurrent.duration._
  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler = system.scheduler

  def uplinkOrg: Route = post {
    path("org") {
      entity(as[OrgKey]) { request =>
        val operationPerformed: Future[Either[Throwable, Int]] = (operations ? request).mapTo[Either[Throwable, Int]]
        onSuccess(operationPerformed) {
          case Right(value) => if (value > 0) complete(StatusCodes.OK) else complete(StatusCodes.AlreadyReported)
          case Left(reason) => complete(StatusCodes.InternalServerError -> reason)
        }
      }
    }
  }

  //  （1）凭证提供机构注册上链（凭证提供机构的公钥地址上链）
  //  in:凭证提供机构ID、公钥数组
  //  out：状态码。200成功

  def lookup: Route = (get & path("org" / Segment)) { orgId =>
      val operationPerformed: Future[Either[Throwable, 公钥数组]] = (operations ? orgId).mapTo[Either[Throwable, 公钥数组]]
      onSuccess(operationPerformed) {
        case Right(value) => complete(StatusCodes.OK, value)
        case Left(reason) => complete(StatusCodes.InternalServerError -> reason)
      }
  }
  //  （2）获取凭证注册时凭证编号对应的公钥对（凭证编号是凭证提供机构的编号）
  //  in：凭证提供机构ID
  //  out：凭证对应公钥数组

  def uplinkDetails: Route = post {
    path("org" / "details") {
      entity(as[UpLink]) { request =>
          val operationPerformed: Future[Either[Throwable, UpLinkResult]] = (operations ? request).mapTo[Either[Throwable, UpLinkResult]]
          onSuccess(operationPerformed) {
            case Right(value) => if (value.upLink === request) complete(StatusCodes.OK, value.address) else complete(StatusCodes.NotFound)
            case Left(reason) => complete(StatusCodes.InternalServerError -> reason)
          }
      }
    }
  }
  //  （3）新签发的凭证，明文哈希和凭证上链。（新的凭证的哈希和明文上链，并返回凭证的链上ID。 凭证的内容中包含有凭证的有效期、凭证所属的CID和其他属性，这里只需要关心其中的有效期和CID属性）
  //  in：CID、凭证提供机构ID、凭证指纹、凭证明文、凭证有效期
  //  out：状态码；凭证的链上ID

  def invalidate: Route = delete {
    path("address" / Segment) { cid =>
        val operationPerformed: Future[Either[Throwable, Int]] = (operations ? cid).mapTo[Either[Throwable, Int]]
        onSuccess(operationPerformed) {
          case Right(value) => if (value > 0) complete(StatusCodes.OK) else complete(StatusCodes.AlreadyReported)
          case Left(reason) => complete(StatusCodes.InternalServerError -> reason)
        }
    }
  }
  //  （4）注销凭证（将指定链上ID对应的凭证状态置为失效）
  //  in：凭证的链上ID、
  //  out：状态码

}
