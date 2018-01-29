package net.wayfarerx.www
package drinks

object Drinks extends Topic {

  override def displayName: String = "drinks"

  override def title: String = "Drinks"

  override def description: String = "Drinking is fun."

  override def footer: Option[Content] =
    Some(em(""""I drink to make other people more interesting." - Ernest Hemingway"""))

  override lazy val children: Vector[Component] = Vector(
    cocktails.Cocktails,
    ingredients.Spirits,
    ingredients.Wines,
    ingredients.Mixers,
    ingredients.Garnishes,
    glasses.Glasses,
    tools.Tools)

}
