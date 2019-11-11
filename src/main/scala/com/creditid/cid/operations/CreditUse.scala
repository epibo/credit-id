package com.creditid.cid.operations

import cats.effect.Sync
import com.creditid.cid.client.TxHashHex
import com.creditid.cid.client.service.OntService
import com.creditid.cid.web.models.request._

/**
 * @author Wei.Chou
 * @version 1.0, 11/11/2019
 */
trait CreditUse[F[_]] {
  def get(n: credit_use): F[TxHashHex]

  def get(n: random): F[TxHashHex]
}

object CreditUse {
  def apply[F[_] : Sync](service: OntService[F]): CreditUse[F] = new CreditUse[F] {
    override def get(n: credit_use): F[TxHashHex] = ???

    override def get(n: random): F[TxHashHex] = ???
  }
}
