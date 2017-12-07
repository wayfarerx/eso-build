package net.wayfarerx.www
package drinks
package ingredients

sealed trait Garnish extends Ingredient {

  override def parent: Composite = Garnishes

}

object Garnish {

  case object MaraschinoCherry extends Garnish {

    override type Measure = Amount.Units

    override def name = "maraschino-cherry"

    override def title = "Maraschino Cherry"

    override def description: String = "A preserved, sweetened cherry, typically made from light-colored sweet " +
      "cherries such as the Royal Ann, Rainier, or Gold varieties."

  }

}

object Garnishes extends Ingredients {

  override def parent: Composite = Drinks

  override def name: String = "garnishes"

  override def title: String = "Garnishes"

  override def image: Image = parent.image

  override def description: String = "A decoration or embellishment used in cocktails."

  override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

  override def components: Vector[Component] = Vector(Garnish.MaraschinoCherry)

}