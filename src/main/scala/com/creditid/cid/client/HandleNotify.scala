package com.creditid.cid.client

import java.util
import java.util.concurrent.atomic.AtomicInteger

import cats.effect.{Sync, Timer}
import cats.implicits._
import com.creditid.cid.client.service.OntService
import com.github.ontio.network.connect.IConnector
import com.github.ontio.sdk.exception.SDKException

import scala.concurrent.duration.DurationInt

/**
 * @author Wei.Chou
 * @version 1.0, 17/10/2019
 */
object HandleNotify {

  def wait4Result[F[_] : Sync : Timer](service: OntService[F], txHash: TxHashHex)(f: Either[Exception, Option[Notify]] => Unit): F[IfSuccess] = {
    def isSuccess(either: Either[Exception, Option[Notify]]): IfSuccess = either.getOrElse(None).isDefined

    service.connectorUse { connector =>
      for {
        either <- wait4Result(connector, txHash, new AtomicInteger(20))
        _ = f(either)
        success = isSuccess(either)
      } yield success
    }
  }

  @throws[Exception]
  private def wait4Result[F[_] : Sync : Timer](connector: IConnector, txHash: TxHashHex, n: AtomicInteger): F[Either[Exception, Option[Notify]]] = {
    // 以下代码重构自`ConnectMgr.waitResult(txHash)`
    def connOps(): Either[Exception, Option[Notify]] = {
      try {
        val event = connector.getSmartCodeEvent(txHash)
        val notify = if (event == null || !event.isInstanceOf[util.Map[_, _]]) {
          val txState = connector.getMemPoolTxState(txHash) // TODO: 这一句没有看到在哪里用
          None
        } else Option(event.asInstanceOf[util.Map[_, _]].get("Notify").asInstanceOf[Notify])
        Right[Exception, Option[Notify]](notify)
      } catch {
        case e: Exception =>
          if (Seq("UNKNOWN TRANSACTION", "getmempooltxstate").forall(e.getMessage.contains(_))) {
            Left[Exception, Option[Notify]](new SDKException(e.getMessage))
          } else Right[Exception, Option[Notify]](None)
      }
    }

    for {
      _ <- Timer[F].sleep(3.seconds)
      notifyE = connOps()
      result <- if (notifyE.isLeft) Sync[F].pure(notifyE)
      else if (notifyE.getOrElse(None).isDefined) Sync[F].pure(notifyE)
      else if (n.decrementAndGet > 0) wait4Result(connector, txHash, n)
      else Sync[F].pure[Either[Exception, Option[Notify]]](Right(None))
    } yield result
  }

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
