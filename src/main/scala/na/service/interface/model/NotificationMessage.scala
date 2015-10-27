package na.service.interface.model

import spray.json._

/**
 * @author nader albert
 * @since  27/10/2015.
 */
case class NotificationMessage(payload :Payload, target: Target, options: Option[Map[String, String]]){
  //require(!text.isEmpty, "text in a notification must not be empty")
}

trait Payload{
  val content :Map[String, String]
}

trait Target {
  // This field can contain a single registration token , a topic, or a notification key (for sending to a device group).
  // Single-App --> Registration Token
  // Group of Apps --> Topic
  // Group of Devices --> Notification key
  val to :String
}

case class TopicTarget(override val to: String) extends Target {
  require(!to.isEmpty, "topic name cannot be empty !")
  //to = "/topics/" + to
}

case class NotificationPayload(override val content: Map[String, String]) extends Payload

case class DataPayload(override val content: Map[String, String]) extends Payload

object NotificationMessage {

  import TopicTarget._

  def read (json: JsValue): NotificationMessage = { //TODO: to be implemented properly
  val jsonFields = JsonParser(json.asInstanceOf[JsString].value).asJsObject.fields

    /*NotificationMessage(jsonFields.get("text").fold("dummy")(_.prettyPrint),
      jsonFields.get("topic").fold(Topic("dummy"))(topic => Topic(topic.prettyPrint))) */

    /*val data = jsonFields.get("data").flatMap(jsValue => Some(
      DataPayload(jsValue.asJsObject.fields.to(List[])
    )
    )

    val notification = jsonFields.get("notification")
    val topic = jsonFields.get("to")

    NotificationMessage(jsonFields.get("data").flatMap(jsValue => DataPayload(jsValue)), ) */

    NotificationMessage(NotificationPayload(Map()), TopicTarget("foo-bar"), None)
  }

  def write(notification: NotificationMessage): JsObject = {
    var notificationFields: Map[String, JsValue] =
      Map.empty[String, JsValue].updated("to", JsString(notification.target.to))

    notificationFields = notification.payload match {
      case dataPayload :DataPayload =>
        notificationFields.updated("data",
          JsObject(dataPayload.content.flatMap(dataItem => List((dataItem._1, JsString(dataItem._2))))))

      case notificationPayload :NotificationPayload =>
        notificationFields.updated("notification",
          //JsObject(notificationPayload.notification.flatMap(_.flatMap(notificationItem => List((notificationItem._1, JsString(notificationItem._2))))).toList))
          JsObject(notificationPayload.content.flatMap(dataItem => List((dataItem._1, JsString(dataItem._2))))))
    }

    //.updated("data", JsObject(notification.payload.data))
    //.updated ("topic", Topic.write(notification.topic))

    JsObject(notificationFields)
  }

  implicit val notificationWriter = JsonWriter.func2Writer(write)
  implicit val notificationReader = JsonReader.func2Reader(read)
}

object TopicTarget {
  def write(topic: TopicTarget): JsObject = JsObject(("to", JsString(topic.to)))

  def read (json: JsValue): TopicTarget = { //TODO: to be implemented properly
    TopicTarget("baby")
  }

  implicit val topicWriter = JsonWriter.func2Writer(write)
  implicit val topicReader = JsonReader.func2Reader(read)
}
