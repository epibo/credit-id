package com.creditid.cid.web

import cats.effect._
import cats.implicits._
import com.creditid.cid.client.service.OntService
import com.creditid.cid.operations._
import com.creditid.cid.web.models.ResqCode._
import com.creditid.cid.web.models._
import com.creditid.cid.web.models.request._
import com.github.ontio.account.Account
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

package object routes {

  final class Routers[F[_] : Sync](dsl: Http4sDsl[F]) {

    import dsl._

    def init(service: OntService[F], accountF: F[Account]): Unit = {
      val R: ContractOps[F] = ContractOps(service, accountF)

      def deploy() = for {
        (success, txHash) <- R.deploy()
        done <- if (success) waitForResult(txHash) else if (jishu) deploy() else Sync[F].pure(false)
        _ <- if (done) init()
      } yield ()

      def init() = for {
        (success, txHash) <- R.init()
        done <- if (success) waitForResult(txHash) else if (jishu) init() else Sync[F].pure(false)
      } yield done

      deploy()
    }


    def orgOps(R: OrgOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "org_register" =>
          if (req.verified)
            for {
              request <- req.as[org_register]
              (success, txHash) <- R.post(request)
              // TODO: 这期间还要等待`Notify`回应。

              resp <- Ok(执行成功.code)
            } yield resp
          else Ok(验签失败.code)

        case req@POST -> Root / "org_upd_pubkey" =>
          for {
            request <- req.as[org_upd_pubkey]
            (success, txHash) <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp

        case req@GET -> Root / "org_get_pubkeys" =>
          for {
            request <- req.as[org_get_pubkeys]
            (success, txHash) <- R.get(request)

            resp <- Ok(txHashHex)
          } yield resp
      }
    }

    def cigOps(R: CidOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "cid_register" =>
          for {
            request <- req.as[cid_register]
            (success, txHash) <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp

        case req@POST -> Root / "cid_record" =>
          for {
            request <- req.as[cid_record]
            (success, txHash) <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp
      }
    }


    def creditOps(R: CreditOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "credit_register" =>
          for {
            request <- req.as[credit_register]
            (success, txHash) <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp

        case req@POST -> Root / "credit_destroy" =>
          for {
            request <- req.as[credit_destroy]
            (success, txHash) <- R.post(request)

            resp <- Ok(txHashHex)
          } yield resp
      }
    }

    def creditUse(R: CreditUse[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@GET -> Root / "credit_use" =>
          for {
            request <- req.as[credit_use]
            (success, txHash) <- R.get(request)

            resp <- Ok(txHashHex)
          } yield resp //resp

        case req@GET -> Root / "random" =>
          for {
            request <- req.as[random]
            random <- R.get(request)
            resp <- Ok(random)
          } yield resp
      }
    }
  }

}
