package net.wayfarerx.www
package drinks
package ingredients

sealed trait Mixer extends Ingredient {

  final override type Measure = Amount.Liquid

  override def parent: Composite = Mixers

}

object Mixer {

  sealed abstract class Bitters extends Mixer {

    override def parent: Composite = AllBitters

  }

  object Bitters {

    case object Angostura extends Bitters {

      override def name: String = "angostura"

      override def title = "Angostura Bitters"

      override def description: String = "A concentrated bitters, or botanically infused alcoholic mixture, made of" +
        " water, 44.7% ethanol, gentian, herbs and spices, by House of Angostura in Trinidad and Tobago."

    }

  }

  object AllBitters extends Ingredients {

    override def parent: Composite = Mixers

    override def name: String = "bitters"

    override def title: String = "Bitters"

    override def image: Image = parent.image

    override def description: String = "Traditionally an alcoholic preparation flavored with botanical matter such " +
      "that the end result is characterized by a bitter, sour, or bittersweet flavor."

    override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

    override def components: Vector[Component] = Vector(Bitters.Angostura)

  }

}

object Mixers extends Ingredients {

  override def parent: Composite = Drinks

  override def name: String = "mixers"

  override def title: String = "Mixers"

  override def image: Image = parent.image

  override def description: String = "An additive used in cocktails."

  override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

  override def components: Vector[Component] = Vector(Mixer.AllBitters)

}