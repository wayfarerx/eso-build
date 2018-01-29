package net.wayfarerx.www
package drinks
package ingredients

sealed trait Wine extends Ingredient {

  final override type Measure = Amount.Liquid

  override def displayName: String = title.toLowerCase

  override def parent: Option[Composite] = Some(Wines)

}

object Wine {

  sealed abstract class Vermouth extends Wine {

    override def parent: Option[Composite] = Some(Vermouths)

    override def description: String = "An aromatized, fortified wine flavored with various botanicals " +
      "(roots, barks, flowers, seeds, herbs, and spices)."

  }

  object Vermouth {

    case object Dry extends Vermouth {

      override def name: String = "dry"

      override def title: String = "Dry Vermouth"

    }

    case object Sweet extends Vermouth {

      override def name: String = "sweet"

      override def title: String = "Sweet Vermouth"

    }

  }

  object Vermouths extends Topic {

    override def parent: Option[Composite] = Some(Wines)

    override def displayName: String = title.toLowerCase

    override def title: String = "Vermouth"

    override def description: String = "An aromatized, fortified wine flavored with various botanicals (roots, " +
      "barks, flowers, seeds, herbs, and spices)."

    override def children: Vector[Component] = Vector(Vermouth.Dry, Vermouth.Sweet)

  }

}

object Wines extends Topic {

  override def parent: Option[Composite] = Some(Drinks)

  override def displayName: String = title.toLowerCase

  override def title: String = "Wine"

  override def description: String = "An alcoholic beverage made from grapes, generally Vitis vinifera, fermented " +
    "without the addition of sugars, acids, enzymes, water, or other nutrients."

  override def children: Vector[Component] = Vector(Wine.Vermouths)

}
