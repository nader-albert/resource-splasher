package na.service.interface.server.publishers.gcm

import com.typesafe.config.{ConfigFactory, Config}
import na.service.interface.model.NotificationMessage
import spray.http.HttpResponse
import akka.actor.Actor
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpHeaders.`Content-Type`
import spray.http._
import akka.pattern.ask
import scala.language.postfixOps
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * @author nader albert
 * @since  7/10/2015.
 */
case class Publish(notification :NotificationMessage)

class NotificationPublisher extends Actor {
  import context.system
  implicit val timeout = Timeout(15 seconds)
  import ExecutionContext.Implicits.global

  override def receive: Receive = {
    case Publish(notification :NotificationMessage) => println("notification received !")
      val caller = sender

      import NotificationPublisher._

      println(toHttpRequest(notification))

      (IO(Http) ? toHttpRequest(notification))
        .mapTo[HttpResponse]
        .onComplete {
        case Success(response: HttpResponse) => {
          println {
            " [status] => " + response.status +
              " [headers] => " + response.headers.foreach(println) +
              " [body] => " + response.entity.data.asString }
          caller ! response
        }
        case Failure(exception) => println(exception getMessage)
      }
  }
}


object NotificationPublisher {

  val config = ConfigFactory load

  val applicationConfig: Config = config getConfig "notification_server"

  val URI = applicationConfig getString "endpoint"
  val serverApiKey = applicationConfig getString "apiKey"

  val senderID = "9480043767"
  val iosApp = "LWNotificationServerIos"
  val androidApp = "LWNotificationServerAndroid"
  val androidPackageName = "lw.notification.android"

  val AUTHORIZATION: String = "Authorization"

  import spray.json._ //provides us with the global toJson function
  import NotificationMessage._ //provides us with the implicit writer, required by the toJson, to convert the NotificationMessage to a Json String

  //To check the validity of the API Key:
  //curl --header "Authorization: key=AIzaSyC2wOKAwFIgvxTfEnV5CXKW5oQfb5Chp4k" --header Content-Type:"application/json" https://gcm-http.googleapis.com/gcm/send \ -d "{\"registration_ids\":[\"ABC\"]}"

  // The app server builds a downstream message request from these fundamental components: the target, the message
  // options, and the payload. These components are common between the GCM HTTP and XMPP connection server protocols.

  // To send notifications, set notification with the necessary predefined set of key options for the user-visible part
  // of the notification message. Optionally, set data with custom key/value pairs to pass additional
  // payload to the client app. GCM will display the notification part on the client app’s behalf. When optional data is
  // provided, it is sent to the client app once user clicks on the notification and opens the client app.

  def toHttpRequest(notification : NotificationMessage) =
    HttpRequest(HttpMethods.POST,
      URI,
      List(`Content-Type`(MediaTypes.`application/json`),
        HttpHeaders.Authorization(GenericHttpCredentials("","key="+serverApiKey))),
      HttpEntity(MediaTypes.`application/json`, notification.toJson.toString))
}

