package com.creditid.cid.client.service

import cats.effect._
import cats.implicits._
import cats.{Applicative, Foldable}
import com.creditid.cid.client.ContractInvoke.ContractBuilding
import com.creditid.cid.client.{IfSuccess, Ont, TxHashHex}
import com.github.ontio.account.Account
import com.github.ontio.common.Helper
import com.github.ontio.core.payload.DeployCode
import com.github.ontio.core.transaction.Transaction
import com.github.ontio.smartcontract.{NeoVm, Vm}

trait OntService[F[_]] {
  def accountOf(label: String, password: String): F[Account]

  /**
   * @return （成功/失败，txHashHex）
   */
  def deploy(tx: DeployCode): F[(IfSuccess, TxHashHex)]

  def build(address: String,
            codeString: String,
            name: String,
            codeVersion: String,
            author: String,
            email: String,
            desp: String,
            payer: String): F[DeployCode]

  def sign[TX <: Transaction](tx: TX, account: Account): F[TX]

  def invokeContract(address: String, account: Account, contract: ContractBuilding): F[TxHashHex]
}

object OntService {
  def apply[F[_] : Sync : Timer](host: String): OntService[F] = new OntService[F] {
    private val ontHost = Ont.apply[F](host)

    override def accountOf(label: String, password: String): F[Account] = {
      ontHost.walletMgr.use { walletMgr =>
        def createAccount = for {
          wltAcc <- Sync[F].delay(walletMgr.createAccount(label, password))
          _ <- Sync[F].delay(walletMgr.writeWallet())
        } yield {
          wltAcc
        }

        for {
          walAcs <- Sync[F].delay(ontHost.accountsFrom(walletMgr).toList)
          wAcc <- Foldable[List].find(walAcs)(_.label == label).fold(createAccount)(Sync[F].pure(_))
        } yield {
          walletMgr.getAccount(wAcc.address, password)
        }
      }
    }

    override def deploy(tx: DeployCode): F[(IfSuccess, TxHashHex)] = {
      val txHex = Helper.toHexString(tx.toArray)
      for {
        // FIXME: `sendRawTransaction`方法会`block`线程，待整改。
        bool <- ontHost.connection.use(conn => Sync[F].delay(conn.sendRawTransaction(txHex)))
        txHash = tx.hash.toHexString
      } yield {
        (bool, txHash)
      }
    }

    override def sign[TX <: Transaction](tx: TX, account: Account): F[TX] = ontHost.signTx(tx, Array(Array(account)))

    override def build(address: String,
                       codeString: String,
                       name: String,
                       codeVersion: String,
                       author: String,
                       email: String,
                       desc: String,
                       payer: String): F[DeployCode] = {

      // 关于 GasPrice, 这里有说明。https://dev-docs.ont.io/#/docs-cn/smartcontract/01-started
      def build(vm: Vm, limit: Long, price: Long) =
        vm.makeDeployCodeTransaction(codeString, true, name, codeVersion, author, email, desc, payer, limit, price)

      for {
        (limit, price) <- Applicative[F].tuple2(ontHost.defaultGasLimit, ontHost.defaultGasPrice)
        deployCode <- ontHost.ofVm(address).use(vm => Sync[F].delay(build(vm.select[Vm].get, limit, price)))
      } yield {
        deployCode
      }
    }

    override def invokeContract(address: String, account: Account, contract: ContractBuilding): F[TxHashHex] = {
      for {
        (limit, price) <- Applicative[F].tuple2(ontHost.defaultGasLimit, ontHost.defaultGasPrice)
        txHash <- ontHost.ofVm().use(vm => Sync[F].delay(contract.sendTx(vm.select[NeoVm].get, address, account, limit, price)))
      } yield {
        txHash
      }
    }
  }
}
