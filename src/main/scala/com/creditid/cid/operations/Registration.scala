package com.creditid.cid.operations

import cats.effect._
import cats.implicits._
import cats.Applicative
import com.creditid.cid.client.ContractInvoke.OrgRegister
import com.creditid.cid.client.service.OntService
import com.creditid.cid.web.models._
import com.creditid.cid.web.models.request.org_register

//  注册流程
trait Registration[F[_]] {
  //  1）CID注册上链（CID的标识上链）
  //  提交个人信息（字符串），返回 CID。
  def post(n: org_register):  F[String]
}

object Registration {


  def apply[F[_] : Sync](service: OntService[F]): Registration[F] = new Registration[F] {
    private val label = "default_account"
    private val password = "PASSWORD default " + "abcdefghijklmn".reverse

    override def post(n: org_register):  F[String] = {
      val request = OrgRegister(n.org_id, n.pubkeys.toList)
      val address = n.sign
      for {
        account <- service.accountOf(label, password)
        tx <- service.invokeContract(address, account, request)
      } yield {
        tx
      }

    }
  }
}

