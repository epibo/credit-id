package com.creditid.cid.client

import java.lang.reflect.Field

import cats.effect.{Resource, Sync}
import com.creditid.cid.client.Ont.OntVm
import com.github.ontio.OntSdk
import com.github.ontio.account.Account
import com.github.ontio.core.transaction.Transaction
import com.github.ontio.network.connect.IConnector
import com.github.ontio.sdk.manager.{ConnectMgr, WalletMgr}
import com.github.ontio.sdk.wallet.{Account => WalletAccount}
import com.github.ontio.smartcontract.{NativeVm, NeoVm, Vm, WasmVm}
import shapeless._

import scala.collection.convert.ImplicitConversionsToScala.`list asScalaBuffer`

private[client] object Ont {
  type OntVm = Vm :+: NativeVm :+: NeoVm :+: WasmVm :+: CNil

  def apply[F[_] : Sync](host: String): Ont[F] = new Ont[F](host)
}

private[client] final class Ont[F[_] : Sync](host: String) {
  private val restUrl = "http://" + host + ":20334"
  private val rpcUrl = "http://" + host + ":20336"
  private val wsUrl = "http://" + host + ":20335"
  private val sdk = OntSdk.getInstance
  sdk.setRpc(rpcUrl)
  sdk.setRestful(restUrl)
  //sdk.setWesocket(wsUrl)
  sdk.setDefaultConnect(sdk.getRestful)

  // 如果没有文件，会自动创建。
  sdk.openWalletFile("credit_id-ont_wallet.dat")

  val defaultGasLimit: F[Long] = Sync[F].pure(sdk.DEFAULT_DEPLOY_GAS_LIMIT)
  val defaultGasPrice: F[Long] = Sync[F].pure(500)

  def connection: Resource[F, (ConnectMgr, IConnector)] = {
    def defaultConMgr: Resource[F, ConnectMgr] = Resource.make(Sync[F].delay(sdk.getConnect))(_ => Sync[F].unit)

    def connectorOf(conMgr: ConnectMgr): IConnector = {
      // 反射取值
      val conFld: Field = classOf[ConnectMgr].getDeclaredFields.filter(_.getType == classOf[IConnector])(0)
      conFld.setAccessible(true)
      conFld.get(conMgr).asInstanceOf[IConnector]
    }
    //def defaultConctr: Resource[F, IConnector] = Resource.make(Sync[F].delay(new RestClient(restUrl)))(_ => Sync[F].unit)

    for {
      conMgr <- defaultConMgr
      conctr = connectorOf(conMgr)
    } yield (conMgr, conctr)
  }

  def signTx[TX <: Transaction](tx: TX, accounts: Array[Array[Account]]): F[TX] = Sync[F].delay(sdk.signTx(tx, accounts).asInstanceOf[TX])

  def ofVm(vmTypeOrAddr: String = "Neo"): Resource[F, OntVm] = {
    val vm = vmTypeOrAddr match {
      case "Native" => Sync[F].delay(Coproduct[OntVm](sdk.nativevm()))
      case "Neo" => Sync[F].delay(Coproduct[OntVm](sdk.neovm()))
      case "Wasm" => Sync[F].delay(Coproduct[OntVm](sdk.wasmvm()))
      case _ => Sync[F].delay(Coproduct[OntVm] {
        sdk.vm.setCodeAddress(vmTypeOrAddr)
        sdk.vm
      })
    }
    Resource.make(vm)(_ => Sync[F].unit)
  }

  def walletMgr: Resource[F, WalletMgr] = {
    val wallet = Sync[F].delay(sdk.getWalletMgr)
    Resource.make(wallet)(_ => Sync[F].unit)
  }

  def accountsFrom(walletMgr: WalletMgr): Seq[WalletAccount] = walletMgr.getWallet.getAccounts.toSeq

  
}
