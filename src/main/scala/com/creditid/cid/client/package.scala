package com.creditid.cid

import com.alibaba.fastjson.JSON
import com.creditid.cid.client.models.ABI_JSON
import com.github.ontio.OntSdk
import com.github.ontio.account.Account
import com.github.ontio.smartcontract.neovm.abi.AbiInfo

import scala.collection.convert.ImplicitConversionsToScala.`list asScalaBuffer`

/**
 * @author Wei.Chou
 * @version 1.0, 16/10/2019
 */
package object client {
  val TEST_MODE = true

  // 文档说：`address`是小端，`hash`是大端。如果要向合约地址转账，就要使用`hash`。
  // TODO: 但实时并非如此：不能加`reverse`。
  lazy val contractAddress: String = abinfo.getHash  //.reverse
  lazy val abinfo: AbiInfo = JSON.parseObject(ABI_JSON, classOf[AbiInfo])

  lazy val account: Account = {
    val wltMgr = ontSdk.getWalletMgr
    wltMgr.getAccount(
      // 下面的返回值类型是：
      // com.github.ontio.sdk.wallet.Account
      wltMgr.getWallet.getAccounts.toSeq.find(_.label == LABEL).fold {
        val wltAcc = wltMgr.createAccount(LABEL, PASSWORD)
        wltMgr.writeWallet()
        wltAcc
      } { wac =>
        wac
      }.address, PASSWORD)
  }

  lazy val ontSdk: OntSdk = {
    val ip = if (TEST_MODE) {
      val array = Array("120.79.231.116", "120.79.147.72", "120.77.45.30", "120.79.80.65")
      array((math.random * 4).toInt)
    } else {
      // FIXME: 待完整
      "x"
    }

    implicit class AsTuple[A](seq: Seq[A]) {
      @inline def asTup3: (A, A, A) = (seq(0), seq(1), seq(2))
    }

    val (restUrl, wsUrl, rpcUrl) = Seq.fill(3)("http://" + ip + ":").zipWithIndex.map { elem =>
      elem._1 + (20334 + elem._2)
    }.asTup3

    //    val restUrl = "http://" + ip + ":" + "20334"
    //    val rpcUrl = "http://" + ip + ":" + "20336"
    //    val wsUrl = "http://" + ip + ":" + "20335"

    val sdk = OntSdk.getInstance
    sdk.setRpc(rpcUrl)
    sdk.setRestful(restUrl)
    sdk.setDefaultConnect(sdk.getRestful)

    // 如果没有文件，会自动创建。
    sdk.openWalletFile("credit_id-ont_wallet.dat")
    sdk
  }

  val LABEL = "default_account"
  val PASSWORD = "PASSWORD default " + "abcdefghijklmn".reverse
}
