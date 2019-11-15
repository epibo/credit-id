package com.creditid.cid

import cats.effect._
import com.alibaba.fastjson.JSON
import com.creditid.cid.client.service.OntService
import com.github.ontio.smartcontract.neovm.abi.AbiInfo

/**
 * @author Wei.Chou
 * @version 1.0, 16/10/2019
 */
package object client {
  type IfSuccess = Boolean
  type TxHashHex = String
  type Notify = String

  val TEST_MODE = true

  def ontService[F[_] : Sync]: OntService[F] = OntService(HOST)

  // 文档说：`address`是小端，`hash`是大端。如果要向合约地址转账，就要使用`hash`。
  // TODO: 但实时并非如此：不能加`reverse`。
  // lazy val contractAddress = Address.AddressFromVmCode(VM_CODE).toHexString
  lazy val contractAddress: String = abinfo.getHash //.reverse
  lazy val abinfo: AbiInfo = JSON.parseObject(models.ABI_JSON, classOf[AbiInfo])

  lazy val HOST: String = if (TEST_MODE) {
    Seq("120.79.231.116", "120.79.147.72", "120.77.45.30", "120.79.80.65")((math.random() * 4).toInt)
  } else {
    // TODO: 正式服务器 ip
    ""
  }

  val LABEL = "default_account"
  val PASSWORD = "PASSWORD default " + "abcdefghijklmn".reverse
}
