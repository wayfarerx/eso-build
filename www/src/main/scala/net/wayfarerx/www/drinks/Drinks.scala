package net.wayfarerx.www
package drinks

import net.wayfarerx.www.drinks.ingredients.{Garnishes, Mixers, Spirits, Wines}

object Drinks extends Topic {

  override def name: String = "drinks"

  override def title: String = "Drinks"

  override def description: String = "Drinking is fun."

  override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

  override lazy val components: Vector[Component] =
    Vector(cocktails.Cocktails, Spirits, Wines, Mixers, Garnishes)

}
