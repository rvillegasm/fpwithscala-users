package co.s4ncampus.fpwithscala.users.domain

import cats.data._
import cats.Monad

class UserService[F[_]](repository: UserRepositoryAlgebra[F], validation: UserValidationAlgebra[F]) {

  def create(user: User)(implicit M: Monad[F]): EitherT[F, UserAlreadyExistsError, User] =
    for {
      _ <- validation.doesNotExist(user)
      saved <- EitherT.liftF(repository.create(user))
    } yield saved

  def getByLegalId(legalId: String): OptionT[F, User] = repository.findByLegalId(legalId)

  def update(user: User)(implicit M: Monad[F]): EitherT[F, UserDoesNotExistError, User] =
    for {
      _ <- validation.exists(user.legalId)
      saved <- EitherT.liftF(repository.update(user))
    } yield saved

  def deleteByLegalId(legalID: String)(implicit M: Monad[F]): EitherT[F, UserDoesNotExistError, Int] =
    for {
      _ <- validation.exists(legalID)
      deleted <- EitherT.liftF(repository.deleteByLegalId(legalID))
    } yield deleted


}

object UserService{
  def apply[F[_]](
                 repositoryAlgebra: UserRepositoryAlgebra[F],
                 validationAlgebra: UserValidationAlgebra[F],
                 ): UserService[F] =
    new UserService[F](repositoryAlgebra, validationAlgebra)
}