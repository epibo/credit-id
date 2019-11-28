package com.creditid.cid

import cats.effect._
import com.alibaba.fastjson.JSON
import com.creditid.cid.client.models.{ABI_JSON, VM_CODE}
import com.creditid.cid.client.service.OntService
import com.creditid.cid.operations.{ContractOps, OrgOps}
import com.github.ontio.common.Address
import com.github.ontio.smartcontract.neovm.abi.AbiInfo
import org.http4s.dsl.Http4sDsl
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import com.creditid.cid.web.models._
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

class OntSpec extends Specification with IOMatchers with Http4sMatchers[IO] with Http4sDsl[IO] {
  val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  implicit val timer: Timer[IO] = IO.timer(executionContext)
  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)
  val service: OntService[IO] = client.ontService

  "Ont Client" should {
    "decipher the address from VM code" in {
      val abinfo = JSON.parseObject(ABI_JSON, classOf[AbiInfo])
      val address = Address.AddressFromVmCode(VM_CODE).toHexString

      abinfo.getHash must_== address
    }

    /*"be able sign tx with accounts " in {
      val execution = ContractOps.apply(service, service.accountOf(client.LABEL, client.PASSWORD)).deploy()
      val (success, txHashHex) = execution.unsafeRunSync()

      success must_== true
    }*/

    // TODO： xxx.account.Account:303 行，强制转换报错： org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
    // TODO： 但为何第一次没有这个异常？

    "be able send tx" in {
      val execution = OrgOps(service, service.accountOf(client.LABEL, client.PASSWORD)).post(
        request.org_register("1AbcdEfghIjklMnopQrstUvwxYz", Seq(("pubkey1", false), ("pubkey2", true)), "hmac256"))
      val (success, txHashHex) = execution.unsafeRunSync()

      success must_== true
    }
  }
}
