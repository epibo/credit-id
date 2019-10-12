package com.creditid.cid.external.blockchain

import cats.effect.{Resource, Sync}
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.Method._
import org.http4s.circe._

package object api {
    private  def registrationUri(cid: String): Uri = uri"" / cid

  final class OntOps[F[_]: Sync](client: Client[F]){

    val dsl = new Http4sClientDsl[F]{}
    import dsl._
    //def registeron[T](cid:String) = client.run()[T](registrationUri(cid))(response => jsonOf(response.body))
  }


  //  def registrationUri(cid: String): Uri = uri"" / cid
//  def registrationUri(cid: String): Uri = uri"" / cid
//  def registrationUri(cid: String): Uri = uri"" / cid
//  def registrationUri(cid: String): Uri = uri"" / cid
//
//  final class ApiOps[F[_]](val client: Resource[F, Client[F]]) extends AnyVal {
//    def resiger(cid: String): F[String] = {
//     // val target = registrationUri(cid)
//     // client.use(c => c.expect(target)(jsonOf[F, List[String]]))
//    }
//
//    def records(name: String): F[String] = {
//      val target =
//        client.use(c => c.expect(target)(jsonOf[F, List[String]]))
//    }
//
//    def hello(name: String): F[String] = {
//      val target =
//        client.use(c => c.expect(target)(jsonOf[F, List[String]]))
//    }
//
//    def hello(name: String): F[String] = {
//      val target =
//        client.use(c => c.expect(target)(jsonOf[F, List[String]]))
//    }
//
//
//    def hello(name: String): F[String] = {
//      val target =
//        client.use(c => c.expect(target)(jsonOf[F, List[String]]))
//    }
//
//
//  }
}
