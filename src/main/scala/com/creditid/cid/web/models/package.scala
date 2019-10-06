package com.creditid.cid.web

import cats.kernel.Eq
import io.circe.generic.JsonCodec


package object models {
  type CID = String
  //备注：CID是自然的身份ID。每个凭证都是属于某一个CID的。
  type 机构ID = String
  type Records = String
  type PersonalDetail = String
  type 公钥数组 = String
  type 凭证指纹 = String
  type 凭证明文 = String
  type 凭证有效期 = String
  type 凭证的链上ID = String
  type 机构签名 = String
  type 凭证状态 = String

  @JsonCodec
  final case class Register(cid: CID, details: PersonalDetail)

  @JsonCodec
  final case class Details(cid: CID, records: Records)

  @JsonCodec
  final case class OrgKey(orgId: 机构ID, key: 公钥数组)


  @JsonCodec
  final case class UpLink(cid: CID, orgId: 机构ID, fingerprint: 凭证指纹, text: 凭证明文, limit: 凭证有效期)

  implicit val upLinkEq = Eq.fromUniversalEquals[UpLink]
  @JsonCodec
  final case class UpLinkResult(upLink: UpLink, address: 凭证的链上ID)

  @JsonCodec
  final case class ValidationInfo(address: 凭证的链上ID, signature: 机构签名)


  @JsonCodec
  final case class ValidateResult(status: 凭证状态, fingerprint: 凭证指纹, text: 凭证明文)

}
