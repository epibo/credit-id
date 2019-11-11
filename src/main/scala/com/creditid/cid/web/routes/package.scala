package com.creditid.cid.web

import cats.effect._
import cats.implicits._
import com.creditid.cid.operations._
import com.creditid.cid.web.models.request._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

package object routes {

  final class Routers[F[_] : Sync](dsl: Http4sDsl[F]) {

    import dsl._

    def orgOps(R: OrgOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "org_register" =>
          for {
            request <- req.as[org_register]
            txHashHex <- R.post(request)
            // TODO: 这期间还要等待`Notify`回应。

            resp <- Ok(txHashHex)
          } yield resp //resp

        case req@POST -> Root / "org_upd_pubkey" =>
          for {
            request <- req.as[org_upd_pubkey]
            txHashHex <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp //resp

        case req@GET -> Root / "org_get_pubkeys" =>
          for {
            request <- req.as[org_get_pubkeys]
            txHashHex <- R.get(request)

            resp <- Ok(txHashHex)
          } yield resp //resp
      }
    }

    def cigOps(R: CidOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "cid_register" =>
          for {
            request <- req.as[cid_register]
            txHashHex <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp //resp

        case req@POST -> Root / "cid_record" =>
          for {
            request <- req.as[cid_record]
            txHashHex <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp //resp
      }
    }


    def creditOps(R: CreditOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "credit_register" =>
          for {
            request <- req.as[credit_register]
            txHashHex <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp //resp

        case req@POST -> Root / "credit_destroy" =>
          for {
            request <- req.as[credit_destroy]
            txHashHex <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp //resp
      }
    }

    def creditUse(R: CreditUse[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@GET -> Root / "credit_use" =>
          for {
            request <- req.as[credit_use]
            txHashHex <- R.get(request)

            resp <- Ok(txHashHex)
          } yield resp //resp

        case req@GET -> Root / "random" =>
          for {
            request <- req.as[random]
            random <- R.get(request)
            resp <- Ok(random)
          } yield resp //resp
      }
    }
  }

}
