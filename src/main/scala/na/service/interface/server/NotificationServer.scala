package na.service.interface.server

import akka.io.IO
import na.service.interface.server.listeners.ConnectionListener
import na.service.interface.server.publishers.gcm.NotificationPublisher
import spray.can.Http
import akka.actor.{Props, ActorSystem}

import scala.util.Try

/**
 * @author nader albert
 * @since  7/10/2015.
 */
object NotificationServer extends App {

  implicit val system = ActorSystem("Notification-Server")

  val notificationPublisher = system.actorOf(Props[NotificationPublisher])

  val notificationListener = system.actorOf(ConnectionListener.props(notificationPublisher))

  /*
   * when a Http.Bind message is sent to the IO Actor, an HttpListener actor is started, which accepts incoming
   * connections and for each one spawns a new HttpServerConnection actor, which then manages the connection for
   * the rest of its lifetime.
   * These connection actors process the requests coming in across their connection and dispatch them as immutable
   * spray-http HttpRequest instances to a “handler” actor provided by your application.
   */

  val host = args.toSeq.find(_ startsWith "-host=" ).fold("localhost")(host => host.substring(host.indexOf("=") + 1))

  val port = args.toSeq.find(_ startsWith "-port=" ).fold("8090")(port => port.substring(port.indexOf("=") + 1))

  IO(Http) ! Http.Bind(notificationListener, interface = host, port = Try(Integer.parseInt(port)). toOption.getOrElse(8090))
}
