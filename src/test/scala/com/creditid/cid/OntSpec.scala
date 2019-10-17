package com.creditid.cid

import cats.effect._
import cats.implicits._
import com.alibaba.fastjson.JSON
import com.creditid.cid.client.models.{ABI_JSON, VM_CODE}
import com.creditid.cid.client.service.OntService
import com.github.ontio.common.Address
import com.github.ontio.smartcontract.neovm.abi.AbiInfo
import org.http4s.{HttpApp, Request, Response}
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.testing.{Http4sMatchers, IOMatchers}
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

class OntSpec extends Specification with IOMatchers with Http4sMatchers[IO] with Http4sDsl[IO] {
  val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  val host = "http://" + Seq("120.79.231.116", "120.79.147.72", "120.77.45.30", "120.79.80.65")((math.random() * 4).toInt)
  val label = "default_account"
  val password = "PASSWORD default " + "abcdefghijklmn".reverse

  implicit val timer: Timer[IO] = IO.timer(executionContext)
  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)
  val ontClient = OntService.apply(host)
  

  "Ont Client" should {
    "be able sign tx with accounts " in {
      val execution = for {
        account <- ontClient.accountOf(label, password)
        address = Address.AddressFromVmCode(VM_CODE).toHexString
        payer = account.getAddressU160.toBase58
        unSignedTx <- ontClient.build(address, VM_CODE, "credit_id", "v1.0", "cid.org", "iots.im@qq.com", "cid.org", payer)
        signedTx <- ontClient.sign(unSignedTx, Array(Array(account)))
      } yield {
        signedTx
      }

      execution.unsafeRunSync() must_== ""

    }

    "decipher the address from VM code" in {
      val abinfo = JSON.parseObject(ABI_JSON, classOf[AbiInfo])
      val address = Address.AddressFromVmCode(VM_CODE).toHexString

      // `address`是小端，`hash`是大端。如果要向合约地址转账，就要使用`hash`。
      address must_== abinfo.getHash.reverse
    }
  }


}