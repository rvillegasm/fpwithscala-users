package co.s4ncampus.fpwithscala.users.infraestructure.repository

import co.s4ncampus.fpwithscala.users.domain._

import cats.data._
import cats.syntax.all._
import doobie._
import doobie.implicits._
import cats.effect.Bracket

object UserSQL {

  def insert(user: User): Update0 = sql"""
    INSERT INTO users (legal_id, first_name, last_name, email, phone)
    VALUES (${user.legalId}, ${user.firstName}, ${user.lastName}, ${user.email}, ${user.phone})
  """.update

  def selectByLegalId(legalId: String): Query0[User] = sql"""
    SELECT id, legal_id, first_name, last_name, email, phone
    FROM users
    WHERE legal_id = $legalId
  """.query[User]

  def updateUser(user: User): Update0 = sql"""
    UPDATE users SET legal_id = ${user.legalId},
    first_name = ${user.firstName},
    last_name = ${user.lastName},
    email = ${user.email},
    phone = ${user.phone}
    WHERE legal_id = ${user.legalId}
 """.update

  def removeByLegalId(legalId: String): Update0 = sql"""
    DELETE FROM users WHERE legal_id = $legalId
  """.update
}

class DoobieUserRepositoryInterpreter[F[_]: Bracket[?[_], Throwable]](val xa: Transactor[F])
    extends UserRepositoryAlgebra[F] {
  import UserSQL._

  def create(user: User): F[User] = 
    insert(user).withUniqueGeneratedKeys[Long]("id").map(id => user.copy(id = id.some)).transact(xa)

  def findByLegalId(legalId: String): OptionT[F, User] = OptionT(selectByLegalId(legalId).option.transact(xa))

  def update(user: User): F[User] = updateUser(user).withUniqueGeneratedKeys[Long]("id").map(id => user.copy(id = id.some)).transact(xa)

  def deleteByLegalId(legalId: String): F[Int] = removeByLegalId(legalId).run.transact(xa)

}

object DoobieUserRepositoryInterpreter {
  def apply[F[_]: Bracket[?[_], Throwable]](xa: Transactor[F]): DoobieUserRepositoryInterpreter[F] =
    new DoobieUserRepositoryInterpreter[F](xa)
}