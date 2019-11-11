package com.creditid.cid.operations

import cats.effect.Sync
import com.creditid.cid.client.TxHashHex
import com.creditid.cid.client.service.OntService
import com.creditid.cid.web.models.request._

/**
 * @author Wei.Chou
 * @version 1.0, 11/11/2019
 */
trait CidOps[F[_]] {
  def post(n: cid_register): F[TxHashHex]

  def post(n: cid_record): F[TxHashHex]
}

object CidOps {
  def apply[F[_] : Sync](service: OntService[F]): CidOps[F] = new CidOps[F] {
    override def post(n: cid_register): F[TxHashHex] = ???

    override def post(n: cid_record): F[TxHashHex] = ???
  }
}
