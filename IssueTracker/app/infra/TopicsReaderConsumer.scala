package infra

import akka.actor._
import common.infra._
import common.tools._

class TopicsReaderConsumer extends Actor {
  def receive = {
    case consume: TopicReaderConsumerMessage =>
      val reader = new ObjectReader(consume.message)
      
      val messageType = reader.stringValue("messageType")
      
      messageType match {
        case "" =>
          println(s"############# TopicsReaderConsumer: RECEIVED MESSAGE: $messageType")

          // get attributes

          // use process manager

        case _ =>
          println(s"TopicsReaderConsumer: RECEIVED MESSAGE: $messageType")
      }
      
    case result: Any =>
      println(s"############# TopicsReaderConsumer: RECEIVED COMPLETION: $result")
  }
}
