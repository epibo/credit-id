package com.creditid.cid.client

import com.github.ontio.smartcontract.neovm.abi.Parameter.Type
import com.github.ontio.smartcontract.neovm.abi.{AbiFunction, Parameter}

/**
 * @author Wei.Chou
 * @version 1.0, 17/10/2019
 */
object ContractInvoke {
  val GAS_LIMIT = ontSdk.DEFAULT_DEPLOY_GAS_LIMIT
  val GAS_PRICE = 500

  /**
   * @return 如果通过【非】`websocket`方式发送交易，则无论`preExec`值为`true/false`，都返回交易`hash`。
   *         如果通过`websocket`方式发送交易，则监听到`Notify`，其结构参见`HandleNotify`。
   */
  //{"name":"Init","parameters":[{"name":"account","type":""}
  def Init(): String = {
    val paramAcc = new Parameter("account", Type.String, contractAddress)
    val function = new AbiFunction("Init", paramAcc)

    // return tx.hash.toHexString, 只有在`preExec = false`的情况下，如果为`true`, 则返回值未知。
    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }

  //{"name":"OrgRegister","parameters":[{"name":"org_id","type":""},{"name":"pubkeys","type":""}]},
  def OrgRegister(org_id: String, pubkeys: java.util.List[String]): String = {
    val param1 = new Parameter("org_id", Type.String, org_id)
    val param2 = new Parameter("pubkeys", Type.Array, pubkeys)
    val function = new AbiFunction("OrgRegister", param1, param2)

    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }

  //{"name":"OrgUpdPubkey","parameters":[{"name":"org_id","type":""},{"name":"pubkey","type":""}]},
  def OrgUpdPubkey(org_id: String, pubkey: String): String = {
    val param1 = new Parameter("org_id", Type.String, org_id)
    val param2 = new Parameter("pubkey", Type.String, pubkey)
    val function = new AbiFunction("OrgUpdPubkey", param1, param2)

    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }

  //{"name":"OrgGetPubkeys","parameters":[{"name":"org_id","type":""}]},
  def OrgGetPubkeys(org_id: String): String = {
    val param = new Parameter("org_id", Type.String, org_id)
    val function = new AbiFunction("OrgGetPubkeys", param)

    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }

  //{"name":"CidRegister","parameters":[{"name":"cid","type":""},{"name":"data","type":""}]},
  def CidRegister(cid: String, data: String): String = {
    val param1 = new Parameter("cid", Type.String, cid)
    val param2 = new Parameter("data", Type.String, data)
    val function = new AbiFunction("CidRegister", param1, param2)

    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }

  //{"name":"CidRecord","parameters":[{"name":"cid","type":""},{"name":"data","type":""}]},
  def CidRecord(cid: String, data: String): String = {
    val param1 = new Parameter("cid", Type.String, cid)
    val param2 = new Parameter("data", Type.String, data)
    val function = new AbiFunction("CidRecord", param1, param2)

    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }

  //{"name":"CreditRegister","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""},{"name":"data","type":""}]},
  def CreditRegister(cid: String, org_id: String, data: String): String = {
    val param1 = new Parameter("cid", Type.String, cid)
    val param2 = new Parameter("org_id", Type.String, org_id)
    val param3 = new Parameter("data", Type.String, data)
    val function = new AbiFunction("CreditRegister", param1, param2, param3)

    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }

  //{"name":"CreditDestroy","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""}]},
  def CreditDestroy(cid: String, org_id: String): String = {
    val param1 = new Parameter("cid", Type.String, cid)
    val param2 = new Parameter("org_id", Type.String, org_id)
    val function = new AbiFunction("CreditDestroy", param1, param2)

    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }

  //{"name":"CreditUse","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""}]},
  def CreditUse(cid: String, org_id: String): String = {
    val param1 = new Parameter("cid", Type.String, cid)
    val param2 = new Parameter("org_id", Type.String, org_id)
    val function = new AbiFunction("CreditUse", param1, param2)

    val txHashHex = ontSdk.neovm().sendTransaction(contractAddress, account, account, GAS_LIMIT, GAS_PRICE, function, false)
    txHashHex.asInstanceOf[String]
  }
}
