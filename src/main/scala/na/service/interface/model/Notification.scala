package na.service.interface.model

import spray.json._

/**
 * @author nader albert
 * @since  7/10/2015.
 */
case class Topic(name: String) {
  require(!name.isEmpty, "name of a topic must not be empty")
}

object Topic {
  def write(topic: Topic): JsObject = JsObject(("name", JsString(topic.name)))

  def read (json: JsValue): Topic = {
    Topic("baby")
  }

  implicit val topicWriter = JsonWriter.func2Writer(write)
  implicit val topicReader = JsonReader.func2Reader(read)
}

case class NotificationMessage(text: String, topic: Topic) {
  require(!text.isEmpty, "text in a notification must not be empty")
}

object NotificationMessage {

  def read (json: JsValue): NotificationMessage = {
    val jsonFields = JsonParser(json.asInstanceOf[JsString].value).asJsObject.fields
    NotificationMessage(jsonFields.get("text").fold("dummy")(_.prettyPrint), jsonFields.get("topic").fold(Topic("dummy"))(topic => Topic(topic.prettyPrint)))
  }

  def write(notification: NotificationMessage): JsObject = {
    val notificationFields: Map[String, JsValue] = Map.empty[String, JsValue]
      .updated ("text", JsString(notification.text))
      .updated ("topic", Topic.write(notification.topic))

    JsObject(notificationFields)
  }

  implicit val notificationWriter = JsonWriter.func2Writer(write)
  implicit val notificationReader = JsonReader.func2Reader(read)
}