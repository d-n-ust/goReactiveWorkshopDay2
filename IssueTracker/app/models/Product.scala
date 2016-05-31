package models

import java.util.UUID

import akka.actor._
import akka.persistence._

// commands
final case class AssociateProduct(name: String, description: String, productId: String, agilePmProductId: String)
final case class CreateProduct(name: String, description: String, productId: String)

// events
final case class ProductAssociated(name: String, description: String, productId: String, agilePmProductId: String)
final case class ProductCreated(name: String, description: String, productId: String)

object Product {
  def nextId: String = UUID.randomUUID.toString
}

class Product(productId: String) extends PersistentActor {

  override def persistenceId = productId

  var state: Option[ProductState] = None

  override def receiveCommand: Receive = {
    case command: AssociateProduct =>
      val associated = ProductAssociated(command.name, command.description, productId, command.agilePmProductId)
      persist(associated) { event =>
        updateWith(event)
        sender ! event
      }
  }

  override def receiveRecover: Receive = {
    case event: ProductAssociated => updateWith(event)
  }

  def updateWith(event: ProductAssociated): Unit = {
    state = Some(ProductState(event.name, event.description, event.productId, event.agilePmProductId))
  }

  def updateWith(event: ProductCreated): Unit = {
    state = Some(ProductState(event.name, event.description, event.productId))
  }
}

protected final case class ProductState(name: String, description: String, productId: String, agilePmProductId: String)