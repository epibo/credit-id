package com.creditid.cid.client

import java.util
import java.util.concurrent.atomic.AtomicInteger

import cats.effect.{Sync, Timer}
import cats.implicits._
import com.creditid.cid.client.service.OntService
import com.creditid.cid.web.models._
import com.github.ontio.network.connect.IConnector
import com.github.ontio.sdk.exception.SDKException

import scala.concurrent.duration.DurationInt
import scala.util.Try

/**
 * @author Wei.Chou
 * @version 1.0, 17/10/2019
 */
object NotifyHandler {
  /** 看到一段关于`alibaba.fastjson`的说明：
   * {
   * 3、有关类库的一些说明：
   * SerializeWriter：相当于StringBuffer
   * JSONArray：相当于List<Object>
   * JSONObject：相当于Map<String, Object>
   * *
   * JSON反序列化没有真正数组，本质类型都是List<Object>。
   * }
   * 说明`Notify`是对象类型而不是字符串。
   * `Notify`是从`com.github.ontio.network.rest.Result`中的`Object Result`字段中取出来的，而
   * `Result`字段已在下面的代码中被明确的当做了`Map`，所以……有上面的结论。
   */
  def wait4Result[F[_] : Sync : Timer, A](service: OntService[F], txHash: TxHashHex)(f: Either[Exception, Option[Notify]] => A): F[A] = {
    service.connectorUse { connector =>
      for {
        either <- wait4Result(connector, txHash, new AtomicInteger(20))
        a = f(either)
      } yield a
    }
  }

  private def wait4Result[F[_] : Sync : Timer](connector: IConnector, txHash: TxHashHex, n: AtomicInteger): F[Either[Exception, Option[Notify]]] = {
    // 以下代码重构自`com.github.ontio.sdk.manager.ConnectMgr.waitResult(txHash)`
    def connOps(): Either[Exception, Option[Notify]] = {
      Try {
        val event = connector.getSmartCodeEvent(txHash)
        val notify =
          if (event == null || !event.isInstanceOf[util.Map[_, _]]) {
            val txState = connector.getMemPoolTxState(txHash) // TODO: 这一句没有看到在哪里用
            None
          } else Option(event.asInstanceOf[util.Map[_, _]].get("Notify").asInstanceOf[Notify])
        Right[Exception, Option[Notify]](notify)
      } fold(e => {
        if (Seq("UNKNOWN TRANSACTION", "getmempooltxstate").forall(e.getMessage.contains(_))) {
          Left[Exception, Option[Notify]](new SDKException(e.getMessage))
        } else Right[Exception, Option[Notify]](None)
      }, v => v)
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

  def isSuccess(either: Either[Exception, Option[Notify]]): IfSuccess = either.getOrElse(None).isDefined

  // 合约中调用：
  // Notify(['FunctionName', cid, True/False])

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
  def Init(): Unit = ???

  // 还要根据`cid`, `org_id`判断请求的对应关系。不需要，`hash`已经标识了唯一性。
  def OrgRegister(either: Either[Exception, Option[Notify]]): IfSuccess = isSuccess(either)

  def OrgUpdPubkey(either: Either[Exception, Option[Notify]]): IfSuccess = isSuccess(either)

  // Notify(['OrgGetPubkeys', org_id, True, list])
  // list.elem: (pubkey, True/False)
  def OrgGetPubkeys(either: Either[Exception, Option[Notify]]): (IfSuccess, 公钥组) = {
    val or = either.getOrElse(None)
    println(or)
    if (or.isDefined) {
      val arr: util.List[AnyRef] = or.get

    } else if (either.isLeft) {

    } else {

    }
    // TODO: 还不知道是什么结构，需要先输出来看看。
    ???
  }

  def CidRegister(either: Either[Exception, Option[Notify]]): IfSuccess = isSuccess(either)

  def CidRecord(either: Either[Exception, Option[Notify]]): IfSuccess = isSuccess(either)

  def CreditRegister(either: Either[Exception, Option[Notify]]): IfSuccess = isSuccess(either)

  def CreditDestroy(either: Either[Exception, Option[Notify]]): IfSuccess = isSuccess(either)

  // TODO: 这个 Notify 有数据。这个接口在链上仅取出数据，然后在服务端处理真正要返回的数据。
  // Notify(['CreditUse', cid, True, map[org_id]])
  // map[org_id]: CreditRegister 的`data`。
  def CreditUse(either: Either[Exception, Option[Notify]]): (IfSuccess, 凭据) = {

    ???
  }
}
