package com.creditid.cid.operations

import cats.effect.Sync
import cats.implicits._
import com.creditid.cid.client
import com.creditid.cid.client.ContractInvoke.Init
import com.creditid.cid.client.models.VM_CODE
import com.creditid.cid.client.service.OntService
import com.creditid.cid.client.{IfSuccess, TxHashHex}
import com.github.ontio.account.Account

/**
 * @author Wei.Chou
 * @version 1.0, 11/11/2019
 */
trait ContractOps[F[_]] {
  /**
   * 发送交易执行`deploy`操作。
   * 要看执行是否成功，还要看`waitResult(txHash)`的`Notify`。
   *
   * @return （成功/失败，txHashHex）
   */
  def deploy(): F[(IfSuccess, TxHashHex)]

  def init(): F[(IfSuccess, TxHashHex)]
}

object ContractOps {
  def apply[F[_] : Sync](service: OntService[F], accountF: F[Account]): ContractOps[F] = new ContractOps[F] {
    override def deploy(): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      payer = account.getAddressU160.toBase58
      name = "credit_id"
      codeVer = "v0.1.0"
      author = "com.creditid.cid"
      email = "iots.im@qq.com"
      unSignedTx <- service.build(address, VM_CODE, payer, name, codeVer, author, email, author)
      signedTx <- service.sign(unSignedTx, account)
      (success, txHashHex) <- service.deploy(signedTx)
    } yield {
      (success, txHashHex)
    }

    override def init(): F[(IfSuccess, TxHashHex)] = for {
      account <- accountF
      address = client.contractAddress
      request = Init(address)
      (success, txHashHex) <- service.invokeContract(address, account, request)
    } yield {
      (success, txHashHex)
    }
  }
}
