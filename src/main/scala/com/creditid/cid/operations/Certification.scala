package com.creditid.cid.operations

import cats.Applicative
import com.creditid.cid.web.models._



//认证流程
trait Certification[F[_]]{
  def post(n: Details): F[Option[CID]]
}

object Certification  {

  def impl[F[_]: Applicative]: Certification[F] = new Certification[F]{
    override def post(n: Details):  F[Option[CID]] = ???
  }

}