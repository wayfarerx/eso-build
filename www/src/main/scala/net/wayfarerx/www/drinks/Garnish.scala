package net.wayfarerx.www
package drinks

sealed trait Garnish extends Ingredient

object Garnish extends Ingredients(Drinks) {

  override def title: String = "Garnishes"

  override def description: Content = "A decoration or embellishment used in cocktails."

  case object MaraschinoCherry extends Ingredient(Garnish) with Garnish {

    override type Measure = Amount.Units

    override def title = "Maraschino cherry"

    override def description: Content = "A preserved, sweetened cherry, typically made from light-colored sweet " +
      "cherries such as the Royal Ann, Rainier, or Gold varieties."

  }

}
