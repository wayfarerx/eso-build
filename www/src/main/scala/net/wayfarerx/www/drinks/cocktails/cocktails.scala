package net.wayfarerx.www
package drinks
package cocktails

import glasses.Glass
import tools.Tool

abstract class Cocktail extends Article {

  def kind: Cocktail.Kind

  def glass: Glass

  def tools: Vector[Tool]

  def components: Vector[Cocktail.Component[Ingredient]]

  def instructions: Vector[Content]

  def references: Vector[Link]

  override def parent: Option[Composite] = Some(Cocktails)

  override def style: Option[String] = Some("cocktail")

  override def headline: Option[String] = kind match {
    case Cocktail.Kind.BeforeDinner => Some("A Before Dinner Cocktail")
  }

  override def author: Option[String] = None

  override def content: Vector[Content] = Vector(
    "Ingredients needed:" ~ ul(components map (c => li(c.amount.toString ~ s" ${c.ingredient.displayName}")): _*),
    "Hardware required:" ~ ul(li(glass.displayName) +: tools.map(t => li(t.displayName)): _*),
    "Assembly procedure:" ~ ol(instructions map (li(_)): _*),
    image
  )

  override def footer: Option[Content] = Some(Sequence(references))

}

object Cocktail {

  case class Component[+T <: Ingredient](ingredient: T, amount: T#Measure)

  sealed trait Kind

  object Kind {

    case object BeforeDinner extends Kind

  }

}

object Cocktails extends Topic {

  override def parent: Option[Composite] = Some(Drinks)

  override def displayName = "cocktails"

  override def title = "Cocktails"

  override def description = "Drinking is fun."

  override def children: Vector[Component] = Vector(Manhattan)

}
