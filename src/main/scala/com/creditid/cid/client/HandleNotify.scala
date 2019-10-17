package com.creditid.cid.client

/**
 * @author Wei.Chou
 * @version 1.0, 17/10/2019
 */
object HandleNotify {
  // 合约中调用：
  // Notify(['FunctionName', cid, True/False])

  // TODO: 参见`ontSdk.getConnect.waitResult(txHash)`以取得通知。

  /*{
    "Action": "getsmartcodeeventbyhash",
    "Desc": "SUCCESS",
    "Error": 0,
    "Result": {
      "TxHash": "03295a1b38573f3a40cf75ae2bdda7e7fb5536f067ff5e47de44aeaf5447259b",
      "State": 1,
      "GasConsumed": 0,
      "Notify": [ // TODO: 就是这个 Notify。
    {
      "ContractAddress": "89abcdef0123456789abcdef0123456789abcdef",
      "States": [
      "02"
      ]
    }
      ]
    },
    "Version": "1.0.0"
  }*/

  // Notify(['Init', True])
  def Init(): Unit = {

  }

  def OrgRegister(): Unit = {

  }

  def OrgUpdPubkey(): Unit = {

  }

  // Notify(['OrgGetPubkeys', org_id, True, list])
  // list.elem: (pubkey, True/False)
  def OrgGetPubkeys(): Unit = {

  }

  def CidRegister(): Unit = {

  }

  def CidRecord(): Unit = {

  }

  def CreditRegister(): Unit = {

  }

  def CreditDestroy(): Unit = {

  }

  // TODO: 这个 Notify 有数据。这个接口在链上仅取出数据，然后在服务端处理真正要返回的数据。
  // Notify(['CreditUse', cid, True, map[org_id]])
  // map[org_id]: CreditRegister 的`data`。
  def CreditUse(): Unit = {

  }
}
