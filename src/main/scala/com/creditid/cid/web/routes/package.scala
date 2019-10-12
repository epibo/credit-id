package com.creditid.cid.web

import cats.effect._
import cats.implicits._
import com.creditid.cid.operations._
import com.creditid.cid.web.models._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

package object routes {

 final class Routers[F[_]: Sync](dsl: Http4sDsl[F]) {
  import dsl._
  def records(C: Certification[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
      case req @ POST -> Root / "record" =>
        for {
          request <- req.as[Details]
          result <- C.post(request)
          resp <- Ok(result.get)
        } yield resp
    }
  }

  def register(R: Registration[F]): HttpRoutes[F] = {

    HttpRoutes.of[F] {
      case req @ POST -> Root / "register" =>
        for {
          request <- req.as[Register]
          result <-  R.post(request)
          resp <- Ok(result.get)
        } yield resp
    }
  }

  def signoffs(S: SignOff[F]): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case req @ POST -> Root / "org" =>
        for {
          request <- req.as[OrgKey]
          result <- S.post(request)
          resp <- Ok(result.get)
        } yield (resp)
      case GET -> Root / "org" / orgId =>
        for {
          result <- S.get(orgId)
          resp <- Ok(result.get)
        } yield resp
      case req @ POST -> Root / "org" / "details"  =>
        for {
          request <- req.as[UpLink]
          result <- S.post(request)
          resp <- Ok(result.get)
        } yield (resp)
      case DELETE -> Root / "address" / cid =>
        for {
          result <- S.delete(cid)
          resp <- Ok(result.get)
        } yield (resp)
    }
  }

  def validation(V: Validation[F]): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case req @ POST -> Root / "cert" =>
        for {
          request <- req.as[ValidationInfo]
          result <- V.post(request)
          resp <- Ok(result.get)
        } yield resp
    }
  }
}
}
