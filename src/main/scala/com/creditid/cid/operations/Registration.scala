package com.creditid.cid.operations

import cats.Applicative
import com.creditid.cid.web.models._

//  注册流程
trait Registration[F[_]] {
  //  1）CID注册上链（CID的标识上链）
  //  提交个人信息（字符串），返回 CID。
  def post(n: Register):  F[Option[CID]]
}

object Registration {

  def impl[F[_] : Applicative]: Registration[F] = new Registration[F] {
    override def post(n: Register):  F[Option[CID]] = ???
  }
}

