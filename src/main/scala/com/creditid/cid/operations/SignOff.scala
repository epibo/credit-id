package com.creditid.cid.operations

import cats.Applicative
import com.creditid.cid.web.models._

trait SignOff[F[_]]{
  def post(n: OrgKey): F[Option[机构ID]]
  def get(n: 机构ID): F[Option[公钥组]]
  def post(n: UpLink): F[Option[UpLinkResult]]
  def delete(n: 凭证的链上ID): F[Option[凭证状态]]
}

object SignOff {

  def apply[F[_] : Applicative]: SignOff[F] = new SignOff[F] {
    override def post(n: OrgKey): F[Option[机构ID]] = ???

    override def get(n: 机构ID): F[Option[公钥组]] = ???

    override def post(n: UpLink): F[Option[UpLinkResult]] = ???

    override def delete(n: 凭证的链上ID): F[Option[凭证状态]] = ???
  }
}
