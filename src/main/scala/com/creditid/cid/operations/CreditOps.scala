package com.creditid.cid.operations

import cats.effect.Sync
import cats.implicits._
import com.creditid.cid.client
import com.creditid.cid.client.ContractInvoke.{CreditDestroy, CreditRegister}
import com.creditid.cid.client.service.OntService
import com.creditid.cid.client.{IfSuccess, TxHashHex}
import com.creditid.cid.web.models.request._
import com.github.ontio.account.Account

/**
 * @author Wei.Chou
 * @version 1.0, 11/11/2019
 */
trait CreditOps[F[_]] {
  def post(n: credit_register): F[(IfSuccess, TxHashHex)]

  def post(n: credit_destroy): F[(IfSuccess, TxHashHex)]
}

object CreditOps {
  def apply[F[_] : Sync](service: OntService[F], accountF: F[Account]): CreditOps[F] = new CreditOps[F] {
    override def post(n: credit_register): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = CreditRegister(n.cid, n.org_id, n.data)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }

    override def post(n: credit_destroy): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = CreditDestroy(n.cid, n.org_id)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }
  }
}
