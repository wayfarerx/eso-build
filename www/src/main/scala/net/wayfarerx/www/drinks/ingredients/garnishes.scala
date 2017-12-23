package net.wayfarerx.www
package drinks
package ingredients

sealed trait Garnish extends Ingredient {

  override def parent: Option[Composite] = Some(Garnishes)

}

object Garnish {

  case object MaraschinoCherry extends Garnish {

    override type Measure = Amount.Units

    override def displayName = "Maraschino cherry"

    override def title = "Maraschino Cherry"

    override def description: String = "A preserved, sweetened cherry, typically made from light-colored sweet " +
      "cherries such as the Royal Ann, Rainier, or Gold varieties."

  }

}

object Garnishes extends Topic {

  override def parent: Option[Composite] = Some(Drinks)

  override def displayName: String = "garnishes"

  override def title: String = "Garnishes"

  override def description: String = "A decoration or embellishment used in cocktails."

  override def children: Vector[Component] = Vector(Garnish.MaraschinoCherry)

}