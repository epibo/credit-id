package com.creditid.cid.operations

import cats.effect._
import cats.implicits._
import com.creditid.cid.client
import com.creditid.cid.client.ContractInvoke.OrgRegister
import com.creditid.cid.client.TxHashHex
import com.creditid.cid.client.service.OntService
import com.creditid.cid.web.models.request._

trait OrgOps[F[_]] {
  def post(n: org_register): F[TxHashHex]

  def post(n: org_upd_pubkey): F[TxHashHex]

  def get(n: org_get_pubkeys): F[TxHashHex]
}

object OrgOps {
  def apply[F[_] : Sync](service: OntService[F]): OrgOps[F] = new OrgOps[F] {
    override def post(n: org_register): F[TxHashHex] = {
      val request = OrgRegister(n.org_id, n.pubkeys.toList)
      val address = client.contractAddress
      for {
        account <- service.accountOf(client.LABEL, client.PASSWORD)
        txHash <- service.invokeContract(address, account, request)
      } yield {
        txHash
      }
    }

    override def post(n: org_upd_pubkey): F[TxHashHex] = ???

    override def get(n: org_get_pubkeys): F[TxHashHex] = ???
  }
}
