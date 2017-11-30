package net.wayfarerx.www
package drinks

sealed trait Mixer extends Ingredient {

  final override type Measure = Amount.Liquid

}

object Mixer extends Ingredients(Drinks) {

  override def title: String = "Mixers"

  override def description: Content = "An additive used in cocktails."

  sealed trait Bitters extends Mixer {

    override def title = "bitters"

  }

  object Bitters extends Ingredients(Mixer) {

    override def title: String = "Bitters"

    override def description: Content = "Traditionally an alcoholic preparation flavored with botanical matter such " +
      "that the end result is characterized by a bitter, sour, or bittersweet flavor."

    case object Angostura extends Ingredient(Bitters) with Bitters {

      override def title = "Angostura bitters"

      override def description: Content = "A concentrated bitters, or botanically infused alcoholic mixture, made of" +
        " water, 44.7% ethanol, gentian, herbs and spices, by House of Angostura in Trinidad and Tobago."

    }

  }

}