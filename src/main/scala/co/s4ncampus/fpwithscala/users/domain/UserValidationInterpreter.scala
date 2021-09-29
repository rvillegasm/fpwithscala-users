package co.s4ncampus.fpwithscala.users.domain

import cats.Applicative
import cats.data.EitherT

class UserValidationInterpreter[F[_]: Applicative](repository: UserRepositoryAlgebra[F])
    extends UserValidationAlgebra[F] {
  def doesNotExist(user: User): EitherT[F, UserAlreadyExistsError, Unit] = 
    repository.findByLegalId(user.legalId).map(UserAlreadyExistsError).toLeft(())

//  def exists(legalId: String): EitherT[F, UserDoesNotExistsError, Unit] =
//    repository.findByLegalId(legalId).map
}

object UserValidationInterpreter {
  def apply[F[_]: Applicative](repository: UserRepositoryAlgebra[F]) =
    new UserValidationInterpreter[F](repository)
}