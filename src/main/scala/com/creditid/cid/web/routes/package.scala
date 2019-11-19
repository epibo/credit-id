package com.creditid.cid.web

import java.lang
import java.util.concurrent.atomic.AtomicInteger

import cats.effect._
import cats.implicits._
import com.creditid.cid.client.HandleNotify.{CreditUse => CidUse, _}
import com.creditid.cid.client.service.OntService
import com.creditid.cid.operations._
import com.creditid.cid.utils.FlagFile.{readerFlag, writeFlag}
import com.creditid.cid.web.models.RespCode._
import com.creditid.cid.web.models._
import com.creditid.cid.web.models.request._
import com.github.ontio.account.Account
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

package object routes {

  final class Routers[F[_] : Sync : Concurrent : Timer](dsl: Http4sDsl[F]) {

    import dsl._

    def init(service: OntService[F], accountF: F[Account]): F[Boolean] = {
      val R: ContractOps[F] = ContractOps(service, accountF)

      lazy val contractCodeDeployed = "contract_code_deployed"
      lazy val contractInited = "contract_inited"

      def deploy(n: => AtomicInteger): F[Boolean] = for {
        (success, txHash) <- R.deploy()
        done <- if (success) wait4Result(service, txHash)(isSuccess) else if (n.decrementAndGet > 0) deploy(n) else Sync[F].pure(false)
        _ <- if (done) writeFlag(contractCodeDeployed, true.toString) else Sync[F].unit
      } yield done

      def init(n: => AtomicInteger): F[Boolean] = for {
        (success, txHash) <- R.init()
        done <- if (success) wait4Result(service, txHash)(isSuccess) else if (n.decrementAndGet > 0) init(n) else Sync[F].pure(false)
        _ <- if (done) writeFlag(contractInited, true.toString) else Sync[F].unit
      } yield done

      def deployed(): F[Boolean] = for {
        line <- readerFlag(contractCodeDeployed)
        deployed = lang.Boolean.valueOf(line)
      } yield deployed

      def inited(): F[Boolean] = for {
        line <- readerFlag(contractInited)
        inited = lang.Boolean.valueOf(line)
      } yield inited

      def ensureDeployed(): F[Boolean] = for {
        deployed <- deployed()
        done <- if (deployed) Sync[F].pure(true) else deploy(new AtomicInteger(3))
      } yield done


      def ensureInited(): F[Boolean] = for {
        inited <- inited()
        done <- if (inited) Sync[F].pure(true) else init(new AtomicInteger(3))
      } yield done

      for {
        deployed <- ensureDeployed()
        inited <- if (deployed) ensureInited() else Sync[F].pure(false)
        done = deployed && inited
      } yield done
    }

    def orgOps(service: OntService[F], R: OrgOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "org_register" =>
          for {
            request <- req.as[org_register]
            verified <- request.verified
            resp <- if (verified) {
              for {
                (success, txHash) <- R.post(request)
                bool <- if (success) wait4Result(service, txHash)(OrgRegister) else Sync[F].pure(false)
                resp <- if (bool) Ok(response.org_register(执行成功)) else Ok(response.org_register(合约调用失败))
              } yield resp
            } else Ok(response.org_register(HMAC验证失败))
          } yield resp

        case req@POST -> Root / "org_upd_pubkey" =>
          for {
            request <- req.as[org_upd_pubkey]
            verified <- request.verified
            resp <- if (verified) {
              for {
                (success, txHash) <- R.post(request)
                bool <- if (success) wait4Result(service, txHash)(OrgUpdPubkey) else Sync[F].pure(false)
                resp <- if (bool) Ok(response.org_upd_pubkey(执行成功)) else Ok(response.org_upd_pubkey(合约调用失败))
              } yield resp
            } else Ok(response.org_upd_pubkey(HMAC验证失败))
          } yield resp

        case req@GET -> Root / "org_get_pubkeys" =>
          for {
            request <- req.as[org_get_pubkeys]
            verified <- request.verified
            resp <- if (verified) {
              for {
                (success, txHash) <- R.get(request)
                (bool, pubkeys) <- if (success) wait4Result(service, txHash)(OrgGetPubkeys) else Sync[F].pure((false, Seq.empty[(公钥, CurrentUsed)]))
                resp <- if (bool) Ok(/*response.org_get_pubkeys(Right(*/ pubkeys /*))*/) else Ok(合约调用失败.value)
              } yield resp
            } else Ok(HMAC验证失败.value)
          } yield resp
      }
    }

    def cigOps(service: OntService[F], R: CidOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "cid_register" =>
          for {
            request <- req.as[cid_register]
            verified <- request.verified
            resp <- if (verified) {
              for {
                (success, txHash) <- R.post(request)
                bool <- if (success) wait4Result(service, txHash)(CidRegister) else Sync[F].pure(false)
                resp <- if (bool) Ok(response.cid_register(执行成功)) else Ok(response.cid_register(合约调用失败))
              } yield resp
            } else Ok(response.cid_register(HMAC验证失败))
          } yield resp

        case req@POST -> Root / "cid_record" =>
          for {
            request <- req.as[cid_record]
            verified <- request.verified
            resp <- if (verified) {
              for {
                (success, txHash) <- R.post(request)
                bool <- if (success) wait4Result(service, txHash)(CidRecord) else Sync[F].pure(false)
                resp <- if (bool) Ok(response.cid_record(执行成功)) else Ok(response.cid_record(合约调用失败))
              } yield resp
            } else Ok(response.cid_record(HMAC验证失败))
          } yield resp
      }
    }


    def creditOps(service: OntService[F], R: CreditOps[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@POST -> Root / "credit_register" =>
          for {
            request <- req.as[credit_register]
            verified <- request.verified
            resp <- if (verified) {
              for {
                (success, txHash) <- R.post(request)
                bool <- if (success) wait4Result(service, txHash)(CreditRegister) else Sync[F].pure(false)
                resp <- if (bool) Ok(response.credit_register(执行成功)) else Ok(response.credit_register(合约调用失败))
              } yield resp
            } else Ok(response.credit_register(HMAC验证失败))
          } yield resp

        case req@POST -> Root / "credit_destroy" =>
          for {
            request <- req.as[credit_destroy]
            verified <- request.verified
            resp <- if (verified) {
              for {
                (success, txHash) <- R.post(request)
                bool <- if (success) wait4Result(service, txHash)(CreditDestroy) else Sync[F].pure(false)
                resp <- if (bool) Ok(response.credit_destroy(执行成功)) else Ok(response.credit_destroy(合约调用失败))
              } yield resp
            } else Ok(response.credit_destroy(HMAC验证失败))
          } yield resp
      }
    }

    def creditUse(service: OntService[F], R: CreditUse[F]): HttpRoutes[F] = {
      HttpRoutes.of[F] {
        case req@GET -> Root / "credit_use" =>
          for {
            request <- req.as[credit_use]
            verified <- request.verified
            resp <- if (verified) {
              for {
                (success, txHash) <- R.get(request)
                (bool, credit) <- if (success) wait4Result(service, txHash)(CidUse) else Sync[F].pure((false, ""))
                resp <- if (bool) Ok(/*response.credit_use(Right(*/ credit /*))*/) else Ok(合约调用失败.value)
              } yield resp
            } else Ok(HMAC验证失败.value)
          } yield resp

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
