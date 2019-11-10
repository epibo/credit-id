package com.creditid.cid.operations

import cats.effect._
import cats.implicits._
import com.creditid.cid.client
import com.creditid.cid.client.ContractInvoke.OrgRegister
import com.creditid.cid.client.service.OntService
import com.creditid.cid.web.models.request.org_register

trait Registration[F[_]] {
  def post(n: org_register): F[String]
}

object Registration {
  def apply[F[_] : Sync](service: OntService[F]): Registration[F] = (n: org_register) => {
    val request = OrgRegister(n.org_id, n.pubkeys.toList)
    val address = client.contractAddress
    for {
      account <- service.accountOf(client.LABEL, client.PASSWORD)
      tx <- service.invokeContract(address, account, request)
    } yield {
      tx
    }
  }
}
