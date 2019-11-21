package com.creditid.cid.web

import cats.data.State
import cats.effect.Sync
import cats.implicits._
import com.creditid.cid.web.models.request.org_get_pubkeys
import com.github.ontio.crypto.Base58
import enumeratum._
import enumeratum.values._
import io.circe.generic.JsonCodec
import org.apache.commons.codec.digest.HmacUtils
import org.bouncycastle.util.encoders.Hex

package object models {
  type JOBJ = String
  type JARR = Seq[JOBJ]
  type HEX = String
  type BASE58 = String
  type CID = BASE58
  type 机构ID = BASE58
  type 个人信息 = JOBJ
  type 记录 = JOBJ
  type 公钥 = HEX
  type CurrentUsed = Boolean
  type 公钥组 = Seq[(公钥, CurrentUsed)]
  type 凭据 = JOBJ
  type HMAC = HEX
  type 随机数 = BigInt
  type 凭据状态 = CreditState
  type 返回状态 = RespCode
  type 随机数用途 = RandomUsage
  
  
  sealed abstract class CreditState(val name: String) extends EnumEntry

  object CreditState extends Enum[CreditState] with CirceEnum[CreditState] {
    override val values = findValues

    case object 有效 extends CreditState("valid")

    case object 无效 extends CreditState("invalid")

    case object 不存在 extends CreditState("not exist")

  }

  sealed abstract class RespCode(val value: Int, val msg: String) extends IntEnumEntry

  object RespCode extends IntEnum[RespCode] with IntCirceEnum[RespCode] {
    override val values = findValues

    case object 执行成功 extends RespCode(0x1, "执行成功")

    case object HMAC验证失败 extends RespCode(0x2, "HMAC验证失败")

    case object 合约调用失败 extends RespCode(0x3, "合约调用失败")

  }

  sealed abstract class RandomUsage(val value: String) extends EnumEntry

  object RandomUsage extends Enum[RandomUsage] with CirceEnum[RandomUsage] {
    override val values = findValues

    case object 请求CreditUse接口 extends RandomUsage("credit_use")

  }

  implicit class CryptoString(baseXx: String) {
    @inline def decodeBase58 = Base58.decode(baseXx)

    /**
     * 主要用于加入了校验码的地址，与 `toBase58WithChecksum` 对应。
     * 注意：这里并没有去掉在 `toBase58WithChecksum` 里面加入的版本号。若要去掉，可在本返回值后面加上 `.tail` 函数。
     */
    @throws[Exception]
    @inline def decodeBase58Checked = Base58.decodeChecked(baseXx)


    @inline def decodeHex: Array[Byte] = Hex.decode(baseXx)

    def toBytes: Array[Byte] = baseXx.getBytes("utf-8")
  }

  implicit class Verify(req: request.Req) {
    def verified[F[_] : Sync]: F[Boolean] = for {
      bool <- Sync[F].delay(verified(req)).handleErrorWith(_ => Sync[F].pure(false))
    } yield bool

    private def verified(req: request.Req): Boolean = (req: @unchecked) match {
      case request.org_register(org_id, pubkeys, hmac) => verifyHmac(hmac, org_id, pubkeys.reduce { (a, b) => (a._1 + b._1, false) }._1)
      case request.org_upd_pubkey(org_id, pkey, hmac) => verifyHmac(hmac, org_id, pkey)
      case request.cid_register(cid, data, hmac) => verifyHmac(hmac, cid, data)
      case request.cid_record(cid, data, hmac) => verifyHmac(hmac, cid, data)
      case request.credit_register(cid, org_id, data, hmac) => verifyHmac(hmac, cid, org_id, data)
      case request.credit_destroy(cid, org_id, hmac) => verifyHmac(hmac, cid, org_id)
      case request.credit_use(cid, org_id, random, hmac) => verifyHmac(hmac, cid, org_id, random)
    }

    private def verifyHmac(hmac: String, data: String*): Boolean = {
      // TODO: 共享的`key`从配置文件中读取，启动命令参数。
      hmac.decodeHex sameElements HmacUtils.hmacSha256(??? /*key*/ , data.reduce((a, b) => a + b))
    }

    /*
    @throws[Exception]
    private def verifySign(msg: Array[Byte], pkey: Array[Byte], sign: Array[Byte]): Boolean = {
      val account = new Account(false, pkey)
      account.verifySignature(msg, sign)
    }
    */
  }

  object request {

    sealed trait Req

    @JsonCodec
    final case class org_register(org_id: 机构ID, pubkeys: 公钥组, hmac: HMAC) extends Req

    @JsonCodec
    final case class org_upd_pubkey(org_id: 机构ID, pubkey: 公钥, hmac: HMAC) extends Req

    @JsonCodec
    final case class org_get_pubkeys(org_id: 机构ID) extends Req

    @JsonCodec
    final case class cid_register(cid: CID, data: 个人信息, hmac: HMAC) extends Req

    @JsonCodec
    final case class cid_record(cid: CID, data: 记录, hmac: HMAC) extends Req

    @JsonCodec
    final case class credit_register(cid: CID, org_id: 机构ID, data: 凭据, hmac: HMAC) extends Req

    @JsonCodec
    final case class credit_destroy(cid: CID, org_id: 机构ID, hmac: HMAC) extends Req

    @JsonCodec
    final case class credit_use(cid: CID, org_id: 机构ID, random: String, hmac: HMAC) extends Req

    @JsonCodec
    final case class random(usage: 随机数用途) extends Req

  }

  object response {

    sealed trait Resp

    @JsonCodec
    final case class org_register(state: 返回状态) extends Resp

    @JsonCodec
    final case class org_upd_pubkey(state: 返回状态) extends Resp

    //@JsonCodec
    final case class org_get_pubkeys(either: Either[RespCode, 公钥组]) extends Resp

    @JsonCodec
    final case class cid_register(state: 返回状态) extends Resp

    @JsonCodec
    final case class cid_record(state: 返回状态) extends Resp

    @JsonCodec
    final case class credit_register(state: 返回状态) extends Resp

    @JsonCodec
    final case class credit_destroy(state: 返回状态) extends Resp

    //@JsonCodec
    final case class credit_use(either: Either[RespCode, 凭据]) extends Resp

    @JsonCodec
    final case class random(random: 随机数) extends Resp

  }

}
