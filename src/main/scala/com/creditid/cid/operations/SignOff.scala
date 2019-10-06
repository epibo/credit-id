package com.creditid.cid.operations

import cats.Applicative
import com.creditid.cid.web.models._

trait SignOff[F[_]]{
  //1) 凭证提供机构注册上链（凭证提供机构的公钥地址上链）
  def post(n: OrgKey): F[Option[机构ID]]
  //2) 获取凭证注册时凭证编号对应的公钥对_
  def get(n: 机构ID): F[Option[公钥数组]]
  //3)新签发的凭证，明文哈希和凭证上链
  def post(n: UpLink): F[Option[UpLinkResult]]
  //4)注销凭证
  def delete(n: 凭证的链上ID): F[Option[凭证状态]]
}

object SignOff {

  def impl[F[_] : Applicative]: SignOff[F] = new SignOff[F] {
    override def post(n: OrgKey): F[Option[机构ID]] = ???

    override def get(n: 机构ID): F[Option[公钥数组]] = ???

    override def post(n: UpLink): F[Option[UpLinkResult]] = ???

    override def delete(n: 凭证的链上ID): F[Option[凭证状态]] = ???
  }

}
