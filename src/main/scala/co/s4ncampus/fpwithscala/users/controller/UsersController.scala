package co.s4ncampus.fpwithscala.users.controller

import co.s4ncampus.fpwithscala.users.domain._

import cats.effect.Sync
import cats.syntax.all._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl


import org.http4s.{EntityDecoder, HttpRoutes}

import co.s4ncampus.fpwithscala.users.domain.User

class UsersController[F[_]: Sync] extends Http4sDsl[F] {

    implicit val userDecoder: EntityDecoder[F, User] = jsonOf

    private def createUser(userService: UserService[F]): HttpRoutes[F] = 
        HttpRoutes.of[F] {
            case req @ POST -> Root =>
                val action = for {
                    user <- req.as[User]
                    result <- userService.create(user).value
                } yield result
                
                action.flatMap {
                    case Right(saved) => Ok(saved.asJson)
                    case Left(UserAlreadyExistsError(existing)) => Conflict(s"The user with legal id ${existing.legalId} already exists")
                }
        }

    private def getUserByLegalId(userService: UserService[F]): HttpRoutes[F] =
        HttpRoutes.of[F] {
            case req @ GET -> Root if req.params.isEmpty => Ok("")
            case GET -> Root / legalId =>
                userService.getByLegalId(legalId).value.flatMap {
                    case Some(user) => Ok(user.asJson)
                    case None => Conflict(s"The user with legal id $legalId does not exist")
                }
        }


    private def updateUser(userService: UserService[F]): HttpRoutes[F] = 
        HttpRoutes.of[F] {
            case req@PUT -> Root => //TODO preguntar si es necesario Root/id
                val action = for {
                    user <- req.as[User]
                    result <- userService.update(user).value
                } yield result

                action.flatMap {
                    case Right(saved) => Ok(saved.asJson)
                    case Left(UserDoesNotExistError(legalId)) => Conflict(s"The user with legal id $legalId does not exist")
                }
        }

    private def deleteUserByLegalId(userService: UserService[F]): HttpRoutes[F] =
        HttpRoutes.of[F] {
            case req @ DELETE -> Root if req.params.isEmpty => Conflict("Please specify the legalId of the user to be deleted")
            case DELETE -> Root / legalId =>
                userService.deleteByLegalId(legalId).value.flatMap {
                    case Right(_) => Ok(s"User with legalId $legalId has been successfully deleted")
                    case Left(UserDoesNotExistError(legalId)) => Conflict(s"The user with legal id $legalId does not exist")
                }
        }

    def endpoints(userService: UserService[F]): HttpRoutes[F] = {
        createUser(userService) <+>
        getUserByLegalId(userService) <+>
        updateUser(userService) <+>
        deleteUserByLegalId(userService)
    }

}

object UsersController {
    def endpoints[F[_]: Sync](userService: UserService[F]): HttpRoutes[F] =
        new UsersController[F].endpoints(userService)
}