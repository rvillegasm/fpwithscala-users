package co.s4ncampus.fpwithscala

import cats.effect.{ContextShift, IO}
import co.s4ncampus.fpwithscala.users.domain.User
import co.s4ncampus.fpwithscala.users.infraestructure.repository.UserSQL._
import doobie.util.transactor.Transactor
import org.scalatest._
import doobie._
import doobie.implicits._


import scala.concurrent.ExecutionContext

class AnalysisTestScalaCheck extends funsuite.AnyFunSuite with matchers.must.Matchers with doobie.scalatest.IOChecker {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    "sa",
    ""
  )

  def createDB():Update0 = sql"""
      CREATE TABLE USERS (
      ID BIGSERIAL PRIMARY KEY,
      LEGAL_ID VARCHAR NOT NULL,
      FIRST_NAME VARCHAR NOT NULL,
      LAST_NAME VARCHAR NOT NULL,
      EMAIL VARCHAR NOT NULL,
      PHONE VARCHAR NOT NULL
     )
    """.update
  createDB().run.transact(transactor).unsafeRunSync()

  test("Check insert") {
    check(insert(User(None, "104", "Rafael", "Villegas", "r@g.com", "123")))
  }

  test("Check get") {
    check(selectByLegalId("104"))
  }

  test("Check update") {
    check(updateUser(User(None, "104", "Rafael", "Villegas", "r@g.com", "123")))
  }

  test("Check delete") {
    check(removeByLegalId("104"))
  }
}
