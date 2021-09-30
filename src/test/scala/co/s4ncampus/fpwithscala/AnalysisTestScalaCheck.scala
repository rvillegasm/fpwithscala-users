package co.s4ncampus.fpwithscala

import cats.effect.{ContextShift, IO}
import co.s4ncampus.fpwithscala.users.domain.{User, UserService, UserValidationInterpreter}
import co.s4ncampus.fpwithscala.users.infraestructure.repository.DoobieUserRepositoryInterpreter
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

  test("Check insert SQL query") {
    check(insert(User(None, "104", "Rafael", "Villegas", "r@g.com", "123")))
  }

  test("Check get SQL query") {
    check(selectByLegalId("104"))
  }

  test("Check update SQL query") {
    check(updateUser(User(None, "104", "Rafael", "Villegas", "r@g.com", "123")))
  }

  test("Check delete SQL query") {
    check(removeByLegalId("104"))
  }

  test("Validate user service creates nonexistent user") {
    val repo = DoobieUserRepositoryInterpreter(transactor)
    val userService = UserService(repo, UserValidationInterpreter(repo))

    val testUser = User(None, "104", "Rafael", "Villegas", "r@g.com", "123")
    val wasUserCreated = userService.create(testUser).isRight.unsafeRunSync()
    assert(wasUserCreated)
  }

  test("Validate user service fails to create preexisting user") {
    val repo = DoobieUserRepositoryInterpreter(transactor)
    val userService = UserService(repo, UserValidationInterpreter(repo))

    val testUser = User(None, "104", "Rafael", "Villegas", "r@g.com", "123")
    val wasErrorReturned = userService.create(testUser).isLeft.unsafeRunSync()
    assert(wasErrorReturned)
  }

  test("Validate user service gets existing user") {
    val repo = DoobieUserRepositoryInterpreter(transactor)
    val userService = UserService(repo, UserValidationInterpreter(repo))
    val wasUserRetrieved = userService.getByLegalId("104").isDefined.unsafeRunSync()
    assert(wasUserRetrieved)
  }

  test("Validate user service fails to get nonexistent user") {
    val repo = DoobieUserRepositoryInterpreter(transactor)
    val userService = UserService(repo, UserValidationInterpreter(repo))
    val isResultEmpty = userService.getByLegalId("0").isEmpty.unsafeRunSync()
    assert(isResultEmpty)
  }

  test("Validate user service updates preexisting user") {
    val repo = DoobieUserRepositoryInterpreter(transactor)
    val userService = UserService(repo, UserValidationInterpreter(repo))

    val updatedField = "1234567"
    val testUserModified = User(None, "104", "Rafael", "Villegas", "r@g.com", updatedField)
    val updateResult = userService.update(testUserModified)

    assert(updateResult.isRight.unsafeRunSync())
    assert(updateResult.value.unsafeRunSync() match {
      case Left(_) => false
      case Right(user) if user.phone == updatedField => true
      case _ => false
    })
  }

  test("Validate user service fails to update nonexistent user") {
    val repo = DoobieUserRepositoryInterpreter(transactor)
    val userService = UserService(repo, UserValidationInterpreter(repo))

    val nonExistentUser = User(None, "0", "p", "f", "p@f.com", "1")
    val wasErrorReturned = userService.update(nonExistentUser).isLeft.unsafeRunSync()

    assert(wasErrorReturned)
  }

  test("Validate user service deletes preexisting user") {
    val repo = DoobieUserRepositoryInterpreter(transactor)
    val userService = UserService(repo, UserValidationInterpreter(repo))
    val wasUserDeleted = userService.deleteByLegalId("104").isRight.unsafeRunSync()
    assert(wasUserDeleted)
  }

  test("Validate user service fails to delete nonexistent user") {
    val repo = DoobieUserRepositoryInterpreter(transactor)
    val userService = UserService(repo, UserValidationInterpreter(repo))
    val wasErrorReturned = userService.deleteByLegalId("0").isLeft.unsafeRunSync()
    assert(wasErrorReturned)
  }
}
