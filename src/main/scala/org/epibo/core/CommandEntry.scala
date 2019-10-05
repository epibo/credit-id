package org.epibo.core

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.effect.Resource
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import enumeratum._
import monix.eval.Task
import org.backuity.clist.{Command, opt}
import akka.http.scaladsl.server.Route
import cats.implicits._
import org.epibo.external.database.repo
import org.epibo.operations.Global
import org.epibo.web.routes
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.io.StdIn

sealed abstract class CommandEntry(name: String, description: String)
  extends Command(name, description)
    with EnumEntry {
  var help = opt[Boolean](default = false, description = "display list of available commands")
}

object CommandEntry extends Enum[CommandEntry] {
  override val values = findValues

  case object Run extends CommandEntry("run", "start application") with  StrictLogging{
    val enviroments = for {
      _ <- Resource.make(Task(logger.info("App starting")))(_ => Task(logger.info("App stopping")))
      system <- Resource.make(Task(ActorSystem("credit-id")))(s => Task(s.terminate()))
      mat <- Resource.make(Task(ActorMaterializer()(system)))(m => Task(m.shutdown()))
      db <- Resource.make(Task(DatabaseConfig.forConfig[JdbcProfile]("creditDB")))(c => Task(c.db.close()))
    } yield (system, mat,db)

    def executionLogic(config:Config)(resource: (ActorSystem, ActorMaterializer, DatabaseConfig[JdbcProfile])): Task[_] = {
      val httpConfig = config.getConfig("http")
      val (httpHost, httpPort) = (httpConfig.getString("host"), httpConfig.getInt("port"))

      implicit val (system, mat, db) = resource
      implicit val operations:ActorRef = system.actorOf(Global.props);
        for {
          h <- Task.defer(Task.fromFuture(Http().bindAndHandle(routes.all, httpHost, httpPort)))
          _ <- Task(logger.info(s"Checkout http://localhost:8080/hello\nPress RETURN to stop...")) *> Task(StdIn.readLine())
          done <- Task(h.unbind())
        } yield done
     
    }
  }

  
}