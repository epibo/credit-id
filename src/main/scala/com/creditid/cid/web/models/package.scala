package com.creditid.cid.web

import cats.kernel.Eq
import io.circe.generic.JsonCodec
import com.creditid.cid.web.models.ResqCode._
import shapeless.:+:

package object models {
  type JOBJ = (String, JOBJ :+: String)
  type JARR = Seq[JOBJ]
  type CID = String
  type 机构ID = String
  type 个人信息 = JOBJ
  type 记录 = JOBJ
  type 公钥 = String
  type 公钥组 = JARR
  type 凭据 = JOBJ
  type 签名 = String
  type 凭据状态 = Credit.状态
  type 返回状态 = ResqCode.tpe
  type 随机数用途 = Random.用途
  type 凭据JOBJ = (("status".type, 凭据状态), 凭据)

  object Credit extends Enumeration {
    type 状态 = State

    case class State(value: String) extends Val

    val 有效 = State("valid")
    val 无效 = State("invalid")
    val 不存在 = State("not exist")
  }

  object Random extends Enumeration {
    type 用途 = Usage

    case class Usage(value: String) extends Val

    val 请求CreditUse接口 = Usage("credit_use")
  }

  object ResqCode extends Enumeration {
    type tpe = Code

    case class Code(code: Int, desc: String) extends Val

    val 执行成功 = Code(0x1, "执行成功")
    val 验签失败 = Code(0x2, "验签失败")
  }

  object request {

    @JsonCodec
    final case class org_register(org_id: 机构ID, pubkeys: 公钥组, sign: 签名) // pubKey[].map(->privKey).foreach(sign(org_id))

    @JsonCodec
    final case class org_upd_pubkey(org_id: 机构ID, pubkey: 公钥, sign: 签名) // pubKey.map(->privKey).foreach(sign(org_id))

    @JsonCodec
    final case class org_get_pubkeys(org_id: 机构ID)

    @JsonCodec
    final case class cid_register(cid: CID, data: 个人信息, sign: 签名) // sign(hash(cid + data))

    @JsonCodec
    final case class cid_record(cid: CID, data: 记录, sign: 签名) // sign(hash(cid + data))

    @JsonCodec
    final case class credit_register(cid: CID, org_id: 机构ID, data: 凭据, sign: 签名) // sign(hash(cid + org_id + data))

    @JsonCodec
    final case class credit_destroy(cid: CID, org_id: 机构ID, sign: 签名) // sign(hash(cid + org_id))

    @JsonCodec
    final case class credit_use(cid: CID, org_id: 机构ID, sign: 签名) // sign(random + cid + org_id)

    @JsonCodec
    final case class random(usage: 随机数用途)

    // TODO: 验签
  }

  object response {

    final case class org_register(state: 返回状态)

    final case class org_upd_pubkey(state: 返回状态)

    final case class org_get_pubkeys(either: Either[验签失败.type, 公钥组])

    final case class cid_register(state: 返回状态)

    final case class cid_record(state: 返回状态)

    final case class credit_register(state: 返回状态)

    final case class credit_destroy(state: 返回状态)

    final case class credit_use(either: Either[验签失败.type, 凭据JOBJ])

    final case class random(random: BigInt)

  }

  implicit val xxxEq = Eq.fromUniversalEquals[Xxx]

}
