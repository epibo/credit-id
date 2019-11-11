package com.creditid.cid.operations

import cats.effect._
import cats.implicits._
import com.creditid.cid.client
import com.creditid.cid.client.ContractInvoke.{OrgGetPubkeys, OrgRegister, OrgUpdPubkey}
import com.creditid.cid.client.service.OntService
import com.creditid.cid.client.{IfSuccess, TxHashHex}
import com.creditid.cid.web.models.request._
import com.github.ontio.account.Account

trait OrgOps[F[_]] {
  def post(n: org_register): F[(IfSuccess, TxHashHex)]

  def post(n: org_upd_pubkey): F[(IfSuccess, TxHashHex)]

  def get(n: org_get_pubkeys): F[(IfSuccess, TxHashHex)]
}

object OrgOps {
  def apply[F[_] : Sync](service: OntService[F], accountF: F[Account]): OrgOps[F] = new OrgOps[F] {
    override def post(n: org_register): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = OrgRegister(n.org_id, n.pubkeys.toList)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }

    override def post(n: org_upd_pubkey): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = OrgUpdPubkey(n.org_id, n.pubkey)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }

    override def get(n: org_get_pubkeys): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = OrgGetPubkeys(n.org_id)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }
  }
}
