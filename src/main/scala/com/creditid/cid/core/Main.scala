package com.creditid.cid.core

import cats.effect._
import cats.implicits._
import com.typesafe.config.ConfigFactory

import monix.eval.{Task, TaskApp}
import org.backuity.clist.Parser
import com.creditid.cid.web.routes

object Main extends TaskApp {

  import CommandEntry._

  override def run(args: List[String]): Task[ExitCode] =
    new Parser(args)
      .withProgramName("credit-id")
      .withHelpCommand("--help")
      .withDescription("")
      .version("1.0")
      .withCommands(values: _*) match {
      case Some(Run) =>
        import Run._
        val config = ConfigFactory.load()
        enviroments.use(executionLogic(config)).as(ExitCode.Success)

      case None => Task.unit.as(ExitCode.Error)
    }
}
