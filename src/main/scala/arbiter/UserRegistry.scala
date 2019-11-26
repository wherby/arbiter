package arbiter

import akka.actor.Actor

import arbiter.UserRegistry._


final case class User(name: String, age: Int, countryOfResidence: String)
final case class Users(users: Seq[User])

object UserRegistry {
  // actor protocol
  sealed trait Command
  final case class GetUsers() extends Command
  final case class CreateUser(user: User) extends Command
  final case class GetUser(name: String) extends Command
  final case class DeleteUser(name: String) extends Command

  final case class GetUserResponse(maybeUser: Option[User])
  final case class ActionPerformed(description: String)
}

class UserRegistry extends Actor{
  var users:Set[User] =Set()
  override def receive: Receive = {
    case GetUsers() =>
      println(s"GET user for ${sender()}")
      sender() ! Users(users.toSeq)
    case CreateUser(user) =>
      sender() ! ActionPerformed(s"User ${user.name} created.")
      users = (users + user)
    case GetUser(name) =>
      sender() ! GetUserResponse(users.find(_.name == name))
    case DeleteUser(name) =>
      sender() ! ActionPerformed(s"User $name deleted.")
      users= users.filterNot(_.name == name)
  }
}
//#user-registry-actor
