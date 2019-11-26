package arbiter

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.util.Timeout
import akka.pattern._
import arbiter.UserRegistry._

//#import-json-formats
//#user-routes-class
class UserRoutes(userRegistry: ActorRef)(implicit val system: ActorSystem) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  private implicit val scheduler = system.scheduler
  implicit val executionContext = system.dispatcher

  def getUsers(): Future[Users] ={
    (userRegistry? GetUsers()).map{
      user=>
        user.asInstanceOf[Users]
    }
  }

  def getUser(name: String): Future[GetUserResponse] =
    userRegistry.ask(GetUser(name)).map{
      response => response.asInstanceOf[GetUserResponse]
    }
  def createUser(user: User): Future[ActionPerformed] =
    userRegistry.ask(CreateUser(user)).map{
      result => result.asInstanceOf[ActionPerformed]
    }
  def deleteUser(name: String): Future[ActionPerformed] =
    userRegistry.ask(DeleteUser(name)).map{
      result => result.asInstanceOf[ActionPerformed]
    }

  //#all-routes
  //#users-get-post
  //#users-get-delete
  val userRoutes: Route =
    pathPrefix("users") {
      concat(
        //#users-get-delete
        pathEnd {
          concat(
            get {
              complete(getUsers())
            },
            post {
              entity(as[User]) { user =>
                onSuccess(createUser(user)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        //#users-get-delete
        //#users-get-post
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-user-info
              rejectEmptyResponse {
                onSuccess(getUser(name)) { response =>
                  complete(response.maybeUser)
                }
              }
              //#retrieve-user-info
            },
            delete {
              //#users-delete-logic
              onSuccess(deleteUser(name)) { performed =>
                complete((StatusCodes.OK, performed))
              }
              //#users-delete-logic
            })
        })
      //#users-get-delete
    }
  //#all-routes
}
