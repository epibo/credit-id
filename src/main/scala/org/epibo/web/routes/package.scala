package org.epibo.web

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

package object routes {

  private def allRoutes(implicit system: ActorSystem, operations: ActorRef): List[Route] = {
    val reg = new Registration()
    val cert = new Certification()
    val signOff = new SignOff()
    val validate = new Validation()
    List(reg.register, cert.records,
      signOff.uplinkOrg, signOff.lookup, signOff.uplinkDetails, signOff.invalidate,
      validate.validate)
  }

  def all(implicit system: ActorSystem, operations: ActorRef): Route = concat(allRoutes: _*)

  //TODO
  //  二、数字信用链系统内部的功能需求（该部分需求可根据实际需要选择实施或用其方式实施）
  //  1. 维持一个CID到凭证的对应关系。A、 CID的添加；B、通过CID查到凭证列表；C、通过凭证查到CID。
  //  2. 同时提供凭证的存证和凭证的状态核验功能。A、凭证的添加；B、凭证的状态查询；C、根据凭证的有效期智能合约控制凭证状态
  //  3. 维持CID到认证记录的关系。A、可以通过CID，查到认证记录列表。

}
