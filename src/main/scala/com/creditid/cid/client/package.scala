package com.creditid.cid

import com.alibaba.fastjson.JSON
import com.github.ontio.OntSdk
import com.github.ontio.account.Account
import com.github.ontio.common.{Address, Helper}
import com.github.ontio.smartcontract.neovm.abi.AbiInfo
import com.creditid.cid.client.ContractCodes._
import com.github.ontio.core.payload.DeployCode
import com.github.ontio.core.transaction.Transaction

import scala.collection.convert.ImplicitConversionsToScala.`list asScalaBuffer`

/**
 * @author Wei.Chou
 * @version 1.0, 16/10/2019
 */
package object client {
  val TEST_MODE = true

  val abinfo = JSON.parseObject(ABI_JSON, classOf[AbiInfo])
  val address = Address.AddressFromVmCode(VM_CODE).toHexString

  // `address`是小端，`hash`是大端。如果要向合约地址转账，就要使用`hash`。
  assert(address == abinfo.getHash.reverse)

  ontSdk.vm.setCodeAddress(address)


  // 关于 GasPrice, 这里有说明。https://dev-docs.ont.io/#/docs-cn/smartcontract/01-started
  val transaction: Transaction = ontSdk.vm.makeDeployCodeTransaction(VM_CODE,
    true, "credit_id", "v1.0", "cid.org", "iots.im@qq.com", "cid.org",
    account.getAddressU160.toBase58, ontSdk.DEFAULT_DEPLOY_GAS_LIMIT, 500)

  ontSdk.signTx(transaction, Array(Array(account)))
  val txHex = Helper.toHexString(transaction.toArray)

  println(transaction.hash.toString)
  val result = ontSdk.getConnect.syncSendRawTransaction(txHex)
  System.out.println(result)
  val txhash = transaction.hash.toHexString

  // println(ontSdk.getConnect().getMemPoolTxCount());
  // println(ontSdk.getConnect().getMemPoolTxState(txhash));
  Thread.sleep(6000)

  val t: DeployCode = ontSdk.getConnect.getTransaction(txhash).asInstanceOf[DeployCode]

  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////

  lazy val account: com.github.ontio.account.Account = {
    val wltMgr = ontSdk.getWalletMgr
    wltMgr.getAccount(
      // 下面的返回值类型是：
      // com.github.ontio.sdk.wallet.Account
      wltMgr.getWallet.getAccounts.toSeq.find(_.label == LABEL).fold {
        val wltAcc = wltMgr.createAccount(LABEL, PASSWORD)
        wltMgr.writeWallet()
        wltAcc
      } {
        _
      }.address, PASSWORD)
  }

  lazy val ontSdk = {
    val httpAddr = "http://" + (if (TEST_MODE) {
      val array = Array("120.79.231.116", "120.79.147.72", "120.77.45.30", "120.79.80.65")
      array((math.random() * 4).toInt)
    } else {
      // FIXME: 待完整
      "x"
    })

    val restUrl = httpAddr + ":" + "20334"
    val rpcUrl = httpAddr + ":" + "20336"
    val wsUrl = httpAddr + ":" + "20335"

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
