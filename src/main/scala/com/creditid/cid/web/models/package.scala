package com.creditid.cid.web

import cats.kernel.Eq
import io.circe.generic.JsonCodec
import enumeratum._
import enumeratum.values._
import shapeless.:+:

package object models {

  type JOBJ = String
  type JARR = Seq[JOBJ]
  type CID = String
  type 机构ID = String
  type 个人信息 = JOBJ
  type 记录 = JOBJ
  type 公钥 = String
  type 公钥组 = JARR
  type 凭据 = JOBJ
  type 签名 = String
  type 凭据状态 = CreditState
  type 返回状态 = ResqCode
  type 随机数用途 = RandomUsage
  type 凭据JOBJ = ((String, 凭据状态), 凭据)

  sealed abstract class CreditState(val name: String) extends EnumEntry

  object CreditState extends Enum[CreditState] with CirceEnum[CreditState] {

    override val values = findValues

    case object 有效 extends CreditState("valid")
    case object 无效 extends CreditState("invalid")
    case object 不存在 extends CreditState("not exist")
  }

  sealed abstract class ResqCode(val value: Int, val name: String)
      extends IntEnumEntry {
    def code: Int = value
  }

  object ResqCode extends IntEnum[ResqCode] with IntCirceEnum[ResqCode] {
    override val values = findValues

    case object 执行成功 extends ResqCode(0x1, "执行成功")
    case object 验签失败 extends ResqCode(0x2, "验签失败")

  }

  sealed abstract class RandomUsage(val name: String) extends EnumEntry

  object RandomUsage extends Enum[RandomUsage] with CirceEnum[RandomUsage] {
    override val values = findValues
    case object 请求CreditUse接口 extends RandomUsage("credit_use")

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

    @JsonCodec
    final case class org_register(state: 返回状态)

    @JsonCodec
    final case class org_upd_pubkey(state: 返回状态)

    //@JsonCodec
    final case class org_get_pubkeys(either: Either[ResqCode.验签失败.type , 公钥组])

    @JsonCodec
    final case class cid_register(state: 返回状态)

    @JsonCodec
    final case class cid_record(state: 返回状态)

    @JsonCodec
    final case class credit_register(state: 返回状态)

    @JsonCodec
    final case class credit_destroy(state: 返回状态)

    // @JsonCodec
    final case class credit_use(either: Either[ResqCode.验签失败.type , 凭据JOBJ])

    @JsonCodec
    final case class random(random: BigInt)

  }

}
