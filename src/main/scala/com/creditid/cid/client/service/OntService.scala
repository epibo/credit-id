package com.creditid.cid.client.service

import cats.effect._
import cats.implicits._
import cats.{Applicative, Foldable}
import com.creditid.cid.client.ContractInvoke.AbiFuncBuilding
import com.creditid.cid.client.{IfSuccess, Ont, TxHashHex}
import com.github.ontio.account.Account
import com.github.ontio.common.Helper
import com.github.ontio.core.payload.DeployCode
import com.github.ontio.core.transaction.Transaction
import com.github.ontio.network.connect.IConnector
import com.github.ontio.smartcontract.{NeoVm, Vm}
import com.creditid.cid.web.models.随机数
import scala.util.Random

trait OntService[F[_]] {
  def accountOf(label: String, password: String): F[Account]

  def build(address: String,
            codeString: String,
            payer: String,
            name: String,
            codeVersion: String,
            author: String,
            email: String,
            desp: String): F[DeployCode]

  def sign[TX <: Transaction](tx: TX, account: Account): F[TX]

  /**
   * 发送交易执行`deploy`操作。
   * 要看执行是否成功，还要看`waitResult(txHash)`的`Notify`。
   *
   * @return （成功/失败，txHashHex）
   */
  def invokeContract(address: String, account: Account, contract: AbiFuncBuilding): F[(IfSuccess, TxHashHex)]

  /**
   * 发送交易执行`deploy`操作。
   * 要看执行是否成功，还要看`waitResult(txHash)`的`Notify`。
   *
   * @return （成功/失败，txHashHex）
   */
  def deploy(tx: DeployCode): F[(IfSuccess, TxHashHex)]

  def connectorUse[A](f: IConnector => F[A]): F[A]

  def nextRand: F[随机数]
}

object OntService {
  def apply[F[_] : Sync](host: String): OntService[F] = new OntService[F] {
    private val ontHost = Ont.apply[F](host)

    override def nextRand: F[随机数] = Sync[F].delay(BigInt(Random.nextLong()))

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

    override def sign[TX <: Transaction](tx: TX, account: Account): F[TX] = ontHost.signTx(tx, Array(Array(account)))

    override def build(address: String,
                       codeString: String,
                       payer: String,
                       name: String,
                       codeVersion: String,
                       author: String,
                       email: String,
                       desc: String): F[DeployCode] = {

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

    override def invokeContract(address: String, account: Account, contract: AbiFuncBuilding): F[(IfSuccess, TxHashHex)] = {
      for {
        (limit, price) <- Applicative[F].tuple2(ontHost.defaultGasLimit, ontHost.defaultGasPrice)
        txHashE <- ontHost.ofVm().use(vm => Sync[F].delay(contract.sendTx(vm.select[NeoVm].get, address, account, limit, price))
          .attempt) // 注意：此处可能有异常抛出，所以加个`attempt`把它变成`Either[Throwable, TxHashHex]`。
        opt = txHashE.toOption
        bool = opt.isDefined
        txHash = opt.orNull
        _ = txHashE.handleError { e =>
          e.printStackTrace() // 输出异常，以便 debug.
          txHash
        }
      } yield {
        (bool, txHash)
      }
    }

    override def deploy(tx: DeployCode): F[(IfSuccess, TxHashHex)] = {
      val txHex = Helper.toHexString(tx.toArray)
      for {
        bool <- ontHost.connection.use { case (conn, _) =>
          Sync[F].delay(conn.sendRawTransaction(txHex)).handleErrorWith { e =>
            e.printStackTrace() // 输出异常，以便 debug.
            Sync[F].pure(false)
          }
        }
        txHash = tx.hash.toHexString
      } yield {
        (bool, txHash)
      }
    }

    override def connectorUse[A](f: IConnector => F[A]): F[A] =
      ontHost.connection.use { case (_, connector) =>
        f(connector)
      }
  }
}
