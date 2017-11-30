package net.wayfarerx.www
package drinks

abstract class Recipe extends Article(Drinks) {

  final type Component[+T <: Ingredient] = Recipe.Component[T]

  final type Style = Recipe.Style

  final def Component: Recipe.Component.type = Recipe.Component

  final def Style: Recipe.Style.type = Recipe.Style

  def style: Style

  def glass: Glass

  def components: Vector[Component[Ingredient]]

  def tools: Vector[Tool]

  def instructions: Vector[Content]

  def references: Vector[Link]

  override def headline: Option[String] = style match {
    case Recipe.Style.BeforeDinner => Some("A Before Dinner Cocktail")
  }

  override def author: Option[String] = None

  override def content: Vector[Content] = Vector(
    "Ingredients needed:" ~ Unordered(components map (c => Item(c.amount.toString ~ s" ${c.ingredient.title}"))),
    "Hardware required:" ~ Unordered(Item(glass.toString) +: tools.map(t => Item(t.toString))),
    "Assembly procedure:" ~ Ordered(instructions map (Item(_)))
  ) ++ image.toVector

  override def related: Vector[Content] = references

}

object Recipe {

  case class Component[+T <: Ingredient](ingredient: T, amount: T#Measure)

  sealed trait Style

  object Style {

    case object BeforeDinner extends Style

  }

}
