package net.wayfarerx.www
package drinks
package cocktails

import net.wayfarerx.www.drinks.ingredients.Ingredient

abstract class Cocktail extends Article {

  final type Component[+T <: Ingredient] = Cocktail.Component[T]

  final type Style = Cocktail.Style

  final def Component: Cocktail.Component.type = Cocktail.Component

  final def Style: Cocktail.Style.type = Cocktail.Style

  def style: Style

  def glass: Glass

  def components: Vector[Component[Ingredient]]

  def tools: Vector[Tool]

  def instructions: Vector[Content]

  def references: Vector[Link]

  override def parent: Composite = Cocktails

  override def headline: Option[String] = style match {
    case Cocktail.Style.BeforeDinner => Some("A Before Dinner Cocktail")
  }

  override def author: Option[String] = None

  override def content: Vector[Content] = Vector(
    "Ingredients needed:" ~ Unordered(components map (c => Item(c.amount.toString ~ s" ${c.ingredient.title}"))),
    "Hardware required:" ~ Unordered(Item(glass.toString) +: tools.map(t => Item(t.toString))),
    "Assembly procedure:" ~ Ordered(instructions map (Item(_))),
    image
  )

  override def related: Vector[Content] = references

}

object Cocktail {

  case class Component[+T <: Ingredient](ingredient: T, amount: T#Measure)

  sealed trait Style

  object Style {

    case object BeforeDinner extends Style

  }

}

object Cocktails extends Subtopic {

  override def parent: Composite = Drinks

  override def name: String = "cocktails"

  override def title: String = "Cocktails"

  override def image: Image = Drinks.image

  override def description: String = "Drinking is fun."

  override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

  override def components: Vector[Component] = Vector(Manhattan)

}
