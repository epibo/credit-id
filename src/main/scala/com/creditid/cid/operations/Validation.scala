package com.creditid.cid.operations

import cats.Applicative
import com.creditid.cid.web.models._

//凭证使用
trait Validation[F[_]]{
  def post(n: ValidationInfo):  F[Option[ValidateResult]]
}

object Validation {
  def impl[F[_] : Applicative]: Validation[F] = new Validation[F] {
    override def post(n: ValidationInfo): F[Option[ValidateResult]] = ???
  }

}
