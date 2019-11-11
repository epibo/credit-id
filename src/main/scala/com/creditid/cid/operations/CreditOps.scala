package com.creditid.cid.operations

import cats.effect.Sync
import com.creditid.cid.client.TxHashHex
import com.creditid.cid.client.service.OntService
import com.creditid.cid.web.models.request._

/**
 * @author Wei.Chou
 * @version 1.0, 11/11/2019
 */
trait CreditOps[F[_]] {
  def post(n: credit_register): F[TxHashHex]

  def post(n: credit_destroy): F[TxHashHex]
}

object CreditOps {
  def apply[F[_] : Sync](service: OntService[F]): CreditOps[F] = new CreditOps[F] {
    override def post(n: credit_register): F[TxHashHex] = ???

    override def post(n: credit_destroy): F[TxHashHex] = ???
  }
}
