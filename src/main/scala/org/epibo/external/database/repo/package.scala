package org.epibo.external.database

import org.epibo.external.database.models._
import slick.jdbc.JdbcProfile

package object repo {

  final class DDL(profile: JdbcProfile) {

    import profile.api._

    private final class Organizations(tag: Tag) extends Table[Organization](tag, "organization") {
      val orgId = column[Long]("id", O.PrimaryKey, O.AutoInc)
      val chainAddress = column[String]("chain_address")
      val * = (orgId, chainAddress) <> (Organization.tupled, Organization.unapply)
    }

    private val orgs = TableQuery[Organizations]

    private val schemas = List(orgs).map(_.schema).reduce(_ ++ _)
    val setup = DBIO.seq(schemas.createIfNotExists)
    //val setupFuture = db.run(setup)
  }


}
