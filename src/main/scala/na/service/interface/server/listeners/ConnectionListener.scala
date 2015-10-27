package na.service.interface.server.listeners

import na.service.interface.model.Notification
import akka.actor.{ActorRef, Props}
import na.service.interface.model._
import na.service.interface.server.publishers.gcm.Publish
import spray.http.HttpHeaders.`Content-Type`
import spray.http._
import spray.httpx.marshalling.ToResponseMarshallable
import spray.httpx.unmarshalling
import spray.routing._

/**
 * @author nader albert
 * @since  7/10/2015.
 *
 * For a list of predefined directives:
 * http://spray.io/documentation/1.2.2/spray-routing/predefined-directives-by-trait/#list-of-predefined-directives-by-trait
 */

class ConnectionListener(publisher :ActorRef) extends HttpServiceActor {
  var notificationMap = Map [Topic, Notification] (Topic("jackpots") -> Notification("jackpot of 10 million this Friday, buy tickets now!", Topic("jackpots")))

  import ConnectionListener._
  import unmarshalling._
  import ContentType._

  /**
   * runRoute takes an implicit RejectionHandler as a parameter. The RejectionHandler acts as the default handler, it is
   * passed a Partial function that matches all the Rejections that were not handled internally by the route..
   *
   * **/
  implicit val rejectionHandler = RejectionHandler {
    case MissingCookieRejection(cookieName) :: _ =>
      complete("No cookies, no service!!!")

    case l::rest /*UnsupportedRequestContentTypeRejection(reason) :: _*/ => {
      println (l)
      println (rest)
      complete("default handler !")
    }
  }

  /** rejections that are not captured by the custom rejection handler will be passed to the default RejectionHandler
    * defined as an implicit parameter to be passed to the runRoute
    * */
  val customRejectionHandler = RejectionHandler {
    case Nil => complete {
      "This path exists"
    }
    case UnsupportedRequestContentTypeRejection(reason) :: _  if reason == "get" => complete("get rejection caught !!")
    case MalformedHeaderRejection(reason,msg,None) :: _ => complete {HttpResponse(StatusCodes.BadRequest, reason + msg)} // we are not doing anything special here, so we could get rid of this custom rejection handler and rely on the base one.
  }

  override def receive: Receive = runRoute {
    path("resource"){
      get{
        complete {
          "welcome to resource API's"
        }
      }
    } ~
    pathPrefix("resource") {
      handleRejections (customRejectionHandler) {
        pathPrefix("notification") {
          post {
            entity(as[Notification]) { notification =>
              headerValue { //note: the function supplied to headerValue should be a Function not a PartialFunction.. it will be called for every header until it matches the one we are looking for.. for other headers it is expected to return None... if it returns None for all header, the request will be rejected with NotFound error
                case `Content-Type`(ct: ContentType) => Some(ct) //if ct.mediaType == MediaTypes.`application/json` => Some (ct)
                case _ => None
              } { case contentType: ContentType if contentType.mediaType == MediaTypes.`application/json` => {
                    notificationMap = notificationMap.updated(notification.topic, notification)
                    println("passing notification to publisher ...")
                    publisher ! Publish(notification)
                    complete {
                      HttpResponse(StatusCodes.Created) // should be sent to the publishing actor, and a response of 200 should be returned, depicting that it will be acted upon later !
                    }
                  }
                  case _ => reject(MalformedHeaderRejection("content type", " must be set to application/json")) //for some reason, the UnsupportedRequestContentTypeRejection rejection doesn't get caught neither by the custom handlers nor by the default handlers
                }
            }
          } ~ get {
            respondWithHeader(`Content-Type`(ContentType(MediaTypes.`application/json`))) {
              parameterMap { parameters =>
                complete {
                  parameters.get("topic").fold(notificationMap.values)(topicName => {
                    notificationMap.get(Topic(topicName)).fold(List.empty[Notification])(notification => List(notification))
                  })
                }
                //reject(UnsupportedRequestContentTypeRejection("get")) //for testing ... should be removed !
              }
            }
          }
        }
      }
    }
  }
}

object ConnectionListener {
  def props(publisher: ActorRef) = Props(classOf[ConnectionListener], publisher)

  import languageFeature.implicitConversions._
  import spray.json._

  import Notification._

  implicit def toNotification(request: HttpEntity): Notification = readNotification(request.data.asString)

  implicit def fromNotifications(notifications: Iterable[Notification]): ToResponseMarshallable =
    HttpResponse(StatusCodes.OK, HttpEntity(ContentType(MediaTypes.`application/json`),
      JsObject(("notifications", JsArray(notifications.map(_.toJson).toList))).toString))

  def readNotification(notification: String) (implicit reader: JsonReader[Notification]) = reader.read(JsString(notification))

  implicit def gcmNotificationMessage(notification :Notification) :NotificationMessage = {
    /*println(NotificationMessage(NotificationPayload(Map().updated("text", notification.text))
      ,TopicTarget("/topics/" + notification.topic.name), None)) */

    NotificationMessage(NotificationPayload(Map().updated("text", notification.text))
      ,TopicTarget("/topics/" + notification.topic.name), None)
  }

}


