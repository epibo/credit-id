package com.creditid.cid.client.service

import cats.syntax._
import cats.implicits._
import cats.effect._
import com.creditid.cid.client.Ont
import com.github.ontio.account.Account
import com.github.ontio.sdk.wallet.{Account => WalletAccount}
import com.github.ontio.common.Helper
import com.github.ontio.core.payload.DeployCode
import com.github.ontio.core.transaction.Transaction
import com.github.ontio.smartcontract.Vm

import scala.concurrent.duration._

trait OntService[F[_]] {
  def accountOf(label: String, password: String): F[Account]

  def deploy(tx: DeployCode): F[DeployCode]

  def build(address: String,
            codeString: String,
            name: String,
            codeVersion: String,
            author: String,
            email: String,
            desp: String,
            payer: String): F[DeployCode]

  def sign(tx: Transaction, accounts: Array[Array[Account]]): F[Transaction]
}

object OntService {
  def apply[F[_] : Sync : Timer](host: String): OntService[F] = new OntService[F] {
    private val ontHost = Ont.apply[F](host)

    override def accountOf(label: String, password: String): F[Account] = {
      ontHost.walletMgr.use { walletMgr =>
        def walletAcc: F[F[WalletAccount]] = for {
          walAcs <- Sync[F].delay(ontHost.accountsFrom(walletMgr))
        } yield {
          if (walAcs.exists(_.label == label)) {
            Sync[F].delay(walAcs.find(_.label == label).get)
          } else {
            for {
              wltAcc <- Sync[F].delay(walletMgr.createAccount(label, password))
              _ <- Sync[F].delay(walletMgr.writeWallet())
            } yield {
              wltAcc
            }
          }
        }

        for {
          fAcc <- walletAcc
          wAcc <- fAcc
        } yield {
          walletMgr.getAccount(wAcc.address, password)
        }
      }
    }

    override def deploy(tx: DeployCode): F[DeployCode] = {
      val txHex = Helper.toHexString(tx.toArray)
      for {
        _ <- ontHost.connection.use(conn => Sync[F].delay(conn.syncSendRawTransaction(txHex)))
        _ <- Sync[F].delay(Timer[F].sleep(6.seconds))
        code <- ontHost.connection.use(conn => Sync[F].delay(conn.getTransaction(txHex).asInstanceOf[DeployCode]))
      } yield {
        code
      }
    }

    override def sign(tx: Transaction, accounts: Array[Array[Account]]): F[Transaction] = ontHost.signTx(tx, accounts)

    override def build(address: String,
                       codeString: String,
                       name: String,
                       codeVersion: String,
                       author: String,
                       email: String,
                       desp: String,
                       payer: String): F[DeployCode] = {

      def build(vm: Vm, limit: Long, price: Long) =
        vm.makeDeployCodeTransaction(codeString, true, name, codeVersion, author, email, desp, payer, limit, price)

      for {
        defaultLimit <- ontHost.defaultGasLimit
        defaultPrice <- ontHost.defaultGasPrice
        deployCode <- ontHost.vm(address).use(vm => Sync[F].delay(build(vm, defaultLimit, defaultPrice)))
      } yield {
        deployCode
      }
    }
  }
}
