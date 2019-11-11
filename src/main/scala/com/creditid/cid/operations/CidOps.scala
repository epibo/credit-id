package com.creditid.cid.operations

import cats.effect.Sync
import cats.implicits._
import com.creditid.cid.client
import com.creditid.cid.client.ContractInvoke.{CidRecord, CidRegister}
import com.creditid.cid.client.service.OntService
import com.creditid.cid.client.{IfSuccess, TxHashHex}
import com.creditid.cid.web.models.request._
import com.github.ontio.account.Account

/**
 * @author Wei.Chou
 * @version 1.0, 11/11/2019
 */
trait CidOps[F[_]] {
  def post(n: cid_register): F[(IfSuccess, TxHashHex)]

  def post(n: cid_record): F[(IfSuccess, TxHashHex)]
}

object CidOps {
  def apply[F[_] : Sync](service: OntService[F], accountF: F[Account]): CidOps[F] = new CidOps[F] {
    override def post(n: cid_register): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = CidRegister(n.cid, n.data)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }

    override def post(n: cid_record): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = CidRecord(n.cid, n.data)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }
  }
}
