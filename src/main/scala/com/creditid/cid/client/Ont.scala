package com.creditid.cid.client

import cats.syntax._
import cats.implicits._
import cats.effect.{Resource, Sync}
import com.creditid.cid.client.Ont.OntVm
import com.github.ontio.OntSdk
import com.github.ontio.core.transaction.Transaction
import com.github.ontio.sdk.manager.{ConnectMgr, WalletMgr}
import com.github.ontio.account.Account
<<<<<<< HEAD
import com.github.ontio.smartcontract.{NativeVm, NeoVm, Vm, WasmVm}
=======
import com.github.ontio.smartcontract.{NeoVm, Vm}
>>>>>>> 6b64316fdfcbbcc696db0adce2b9ad14e7ce893c
import com.github.ontio.sdk.wallet.{Account => WalletAccount}
import shapeless._

import collection.JavaConverters._
import scala.collection.convert.ImplicitConversionsToScala.`list asScalaBuffer`

private[client] object Ont {
  type OntVm = Vm :+: NativeVm :+: NeoVm :+: WasmVm :+: CNil

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

<<<<<<< HEAD
  val defaultGasLimit: F[Long] = Sync[F].pure(sdk.DEFAULT_DEPLOY_GAS_LIMIT)
  val defaultGasPrice: F[Long] = Sync[F].pure(500)
=======
  def defaultGasLimit: F[Long] = Sync[F].pure(sdk.DEFAULT_DEPLOY_GAS_LIMIT)

  def defaultGasPrice: F[Long] = Sync[F].pure(500)
>>>>>>> 6b64316fdfcbbcc696db0adce2b9ad14e7ce893c

  def connection: Resource[F, ConnectMgr] = {
    val conn = Sync[F].delay(sdk.getConnect)
    Resource.make(conn)(_ => Sync[F].unit)
  }

  def vm(address: String): Resource[F, Vm] = {
    val vm = Sync[F].delay(sdk.vm)
    Resource.make(vm)(_ => Sync[F].unit).map { vm => vm.setCodeAddress(address); vm }
  }

<<<<<<< HEAD
  def ofVM(vmType: String): Resource[F, OntVm] = {
    val vm = vmType match {
      case "Native" => Sync[F].delay(Coproduct[OntVm](sdk.nativevm()))
      case "Neo" => Sync[F].delay(Coproduct[OntVm](sdk.neovm()))
      case "Wasm" => Sync[F].delay(Coproduct[OntVm](sdk.wasmvm()))
      case _ => Sync[F].delay(Coproduct[OntVm](sdk.vm()))
=======
  def vmTx(): Resource[F, NeoVm] = {
    val vm = Sync[F].delay {
      sdk.neovm()
    }
    Resource.make(vm)(_ => Sync[F].unit)
  }

  def walletMgr: Resource[F, WalletMgr] = {
    val wallet = Sync[F].delay {
      sdk.getWalletMgr
>>>>>>> 6b64316fdfcbbcc696db0adce2b9ad14e7ce893c
    }
    Resource.make(vm)(_ => Sync[F].unit)
  }

  def walletMgr: Resource[F, WalletMgr] = {
    val wallet = Sync[F].delay(sdk.getWalletMgr)
    Resource.make(wallet)(_ => Sync[F].unit)
  }

  def accountsFrom(walletMgr: WalletMgr): Seq[WalletAccount] = walletMgr.getWallet.getAccounts.toSeq
}
