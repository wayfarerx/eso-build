package net.wayfarerx.www
package drinks

sealed trait Wine extends Ingredient {

  final override type Measure = Amount.Liquid

}

object Wine extends Ingredients(Drinks) {

  override def title: String = "Wines"

  override def description: Content = "An alcoholic beverage made from grapes, generally Vitis vinifera, fermented " +
    "without the addition of sugars, acids, enzymes, water, or other nutrients."

  trait Vermouth extends Wine {

    override def title: String = "vermouth"

    override def description: Content = "An aromatized, fortified wine flavored with various botanicals " +
      "(roots, barks, flowers, seeds, herbs, and spices)."

  }

  object Vermouth extends Ingredients(Wine) {

    override def title: String = "Vermouth"

    override def description: Content = "An aromatized, fortified wine flavored with various botanicals (roots, " +
      "barks, flowers, seeds, herbs, and spices)."

    override def imageDescription: Option[String] = None

    case object Dry extends Ingredient(Vermouth) with Vermouth {

      override def title: String = "dry vermouth"

    }

    case object Sweet extends Ingredient(Vermouth) with Vermouth {

      override def title: String = "sweet vermouth"

    }

  }

}
