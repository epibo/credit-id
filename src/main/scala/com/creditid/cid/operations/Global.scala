package com.creditid.cid.operations

import akka.actor.{Actor, Props}

final class Global extends Actor {
  override def receive: Receive = PartialFunction.empty
}

object Global {
  def props: Props = Props(new Global())
}
