package co.s4ncampus.fpwithscala

import cats.effect.{ContextShift, IO}
import co.s4ncampus.fpwithscala.users.domain.User
import co.s4ncampus.fpwithscala.users.infraestructure.repository.UserSQL.insert
import doobie.util.transactor.Transactor
import org.scalatest._

import scala.concurrent.ExecutionContext

class AnalysisTestScalaCheck extends funsuite.AnyFunSuite with matchers.must.Matchers with doobie.scalatest.IOChecker {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", ""
  )

  test("Check insert") {
    check(insert(User(None, "104", "Rafael", "Villegas", "r@g.com", "123")))
  }

}
