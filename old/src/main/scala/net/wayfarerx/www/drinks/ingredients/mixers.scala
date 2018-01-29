package net.wayfarerx.www
package drinks
package ingredients

sealed trait Mixer extends Ingredient {

  final override type Measure = Amount.Liquid

  override def parent: Option[Composite] = Some(Mixers)

}

object Mixer {

  sealed abstract class Bitters extends Mixer {

    override def parent: Option[Composite] = Some(AllBitters)

  }

  object Bitters {

    case object Angostura extends Bitters {

      override def name: String = "angostura"

      override def displayName: String = "Angostura bitters"

      override def title = "Angostura Bitters"

      override def description: String = "A concentrated bitters, or botanically infused alcoholic mixture, made of" +
        " water, 44.7% ethanol, gentian, herbs and spices, by House of Angostura in Trinidad and Tobago."

    }

  }

  object AllBitters extends Topic {

    override def parent: Option[Composite] = Some(Mixers)

    override def displayName: String = "bitters"

    override def title: String = "Bitters"

    override def description: String = "Traditionally an alcoholic preparation flavored with botanical matter such " +
      "that the end result is characterized by a bitter, sour, or bittersweet flavor."

    override def children: Vector[Component] = Vector(Bitters.Angostura)

  }

}

object Mixers extends Topic {

  override def parent: Option[Composite] = Some(Drinks)

  override def displayName: String = "mixers"

  override def title: String = "Mixers"

  override def description: String = "An additive used in cocktails."

  override def children: Vector[Component] = Vector(Mixer.AllBitters)

}