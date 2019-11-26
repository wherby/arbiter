package arbiter

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.io.StdIn
import scala.util.Failure
import scala.util.Success


//#main-class
object QuickstartApp {
  //#start-http-server
  private def startHttpServer(routes: Route, system: ActorSystem): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    implicit val classicSystem: akka.actor.ActorSystem = system
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val futureBinding = Http().bindAndHandle(routes, "localhost", 8080)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
  //#start-http-server
  def main(args: Array[String]): Unit = {

    val system = ActorSystem("clustering-cluster", DoraConf.config(1610,"Aebiter"))

    val userRegistryActor = system.actorOf(Props(new UserRegistry), "UserRegistryActor")

    val routes = new UserRoutes(userRegistryActor)(system)
    startHttpServer(routes.userRoutes, system)

    println(s"Server online at Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

  }
}
//#main-class
