package com.creditid.cid.client

import cats.syntax._
import cats.implicits._
import cats.effect.{Resource, Sync}
import com.github.ontio.OntSdk
import com.github.ontio.core.transaction.Transaction
import com.github.ontio.sdk.manager.{ConnectMgr, WalletMgr}
import com.github.ontio.account.Account
import com.github.ontio.smartcontract.Vm
import com.github.ontio.sdk.wallet.{Account => WalletAccount}

import collection.JavaConverters._
import scala.collection.convert.ImplicitConversionsToScala.`list asScalaBuffer`

private[client] object Ont {
  def apply[F[_] : Sync](httpAddr: String): Ont[F] = new Ont[F](httpAddr)
}

private[client] final class Ont[F[_] : Sync](host: String) {
  private val restUrl = host + ":20334"
  private val rpcUrl = host + ":20336"
  private val wsUrl = host + ":20335"
  private val sdk = OntSdk.getInstance
  sdk.setRpc(rpcUrl)
  sdk.setRestful(restUrl)
  sdk.setDefaultConnect(sdk.getRestful)

  // 如果没有文件，会自动创建。
  sdk.openWalletFile("credit_id-ont_wallet.dat")

  def signTx(tx: Transaction, accounts: Array[Array[Account]]): F[Transaction] = Sync[F].delay(sdk.signTx(tx, accounts))

  def defaultGasLimit: F[Long] = Sync[F].pure(sdk.DEFAULT_DEPLOY_GAS_LIMIT)
  def defaultGasPrice: F[Long] = Sync[F].pure(500)

  def connection: Resource[F, ConnectMgr] = {
    val conn = Sync[F].delay {
      sdk.getConnect
    }
    Resource.make(conn)(_ => Sync[F].unit)
  }

  def vm(address: String): Resource[F, Vm] = {
    val vm = Sync[F].delay {
      sdk.vm()
    }
    Resource.make(vm)(_ => Sync[F].unit).map { vm => vm.setCodeAddress(address); vm }
  }

  def walletMgr: Resource[F, WalletMgr] = {
    val wallet = Sync[F].delay {
      sdk.getWalletMgr
    }
    Resource.make(wallet)(_ => Sync[F].unit)
  }

  def accountsFrom(walletMgr: WalletMgr): Seq[WalletAccount] = walletMgr.getWallet.getAccounts.toSeq
}
