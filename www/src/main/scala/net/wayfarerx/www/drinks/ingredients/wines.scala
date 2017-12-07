package net.wayfarerx.www
package drinks
package ingredients

sealed trait Wine extends Ingredient {

  final override type Measure = Amount.Liquid

  override def parent: Composite = Wines

}

object Wine {

  sealed abstract class Vermouth extends Wine {

    override def parent: Composite = Vermouths

    override def title: String = "vermouth"

    override def description: String = "An aromatized, fortified wine flavored with various botanicals " +
      "(roots, barks, flowers, seeds, herbs, and spices)."

  }

  object Vermouth {

    case object Dry extends Vermouth {

      override def name: String = "dry"

      override def title: String = "dry vermouth"

    }

    case object Sweet extends Vermouth {

      override def name: String = "sweet"

      override def title: String = "sweet vermouth"

    }

  }

  object Vermouths extends Ingredients {

    override def parent: Composite = Wines

    override def name: String = "vermouth"

    override def title: String = "Vermouth"

    override def image: Image = parent.image

    override def description: String = "An aromatized, fortified wine flavored with various botanicals (roots, " +
      "barks, flowers, seeds, herbs, and spices)."

    override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

    override def components: Vector[Component] = Vector(Vermouth.Dry, Vermouth.Sweet)

  }

}

object Wines extends Ingredients {

  override def parent: Composite = Drinks

  override def name: String = "wine"

  override def title: String = "Wine"

  override def image: Image = parent.image

  override def description: String = "An alcoholic beverage made from grapes, generally Vitis vinifera, fermented " +
    "without the addition of sugars, acids, enzymes, water, or other nutrients."

  override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

  override def components: Vector[Component] = Vector(Wine.Vermouths)

}
