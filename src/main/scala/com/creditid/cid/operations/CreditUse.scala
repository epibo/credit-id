package com.creditid.cid.operations

import cats.effect.Sync
import cats.implicits._
import com.creditid.cid.client
import com.creditid.cid.client.ContractInvoke.{CreditUse => CidUse}
import com.creditid.cid.client.service.OntService
import com.creditid.cid.client.{IfSuccess, TxHashHex}
import com.creditid.cid.web.models.request._
import com.github.ontio.account.Account

/**
 * @author Wei.Chou
 * @version 1.0, 11/11/2019
 */
trait CreditUse[F[_]] {
  def get(n: credit_use): F[(IfSuccess, TxHashHex)]

  def get(n: random): F[(IfSuccess, TxHashHex)]
}

object CreditUse {
  def apply[F[_] : Sync](service: OntService[F], accountF: F[Account]): CreditUse[F] = new CreditUse[F] {
    override def get(n: credit_use): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = CidUse(n.cid, n.org_id)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }

    override def get(n: random): F[(IfSuccess, TxHashHex)] = ??? // TODO: 这个不用调合约
  }
}
