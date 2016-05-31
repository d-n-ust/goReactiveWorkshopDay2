package models

import akka.actor._
import java.util.UUID

final case class AssociateProduct(name: String, description: String, agilePmProductId: String)

object ProductsController {
  def apply(system: ActorSystem): ActorRef = {
    system.actorOf(Props[ProductsController], "ProductsController-" + UUID.randomUUID.toString)
  }
}

class ProductsController extends Actor {
  def receive = {
    case assocProd: AssociateProduct =>
      val productId = Product.nextId.toString
      val product = context.system.actorOf(Props(classOf[Product], productId), productId)
      product ! AssociateProduct(assocProd.name, assocProd.description, assocProd.agilePmProductId)
      context.become(forumStartedListener(StartForumDiscussionInfo(sender, start, productId)))
  }

  def forumStartedListener(info: StartForumDiscussionInfo): Receive = {
    case event: ProductAssociated =>
      val discussionId = UUID.randomUUID.toString
      val discussion = context.system.actorOf(Props(classOf[Discussion], discussionId), discussionId)
      discussion ! StartDiscussion(info.start.ownerId, info.start.category, info.start.discussionSubject, s"Discussion: ${info.start.discussionSubject}")
      context.become(discussionStartedListener(info.withDiscussionId(discussionId)))
  }

  def discussionStartedListener(info: StartForumDiscussionInfo): Receive = {
    case event: DiscussionStarted =>
      info.sender ! StartForumDiscussionResult(info.forumId, info.discussionId, info.start.ownerId, info.start.category, info.start.forumTopic, info.start.discussionSubject)
  }
}

