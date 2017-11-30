package net.wayfarerx.www
package drinks

sealed trait Spirit extends Ingredient {

  final override type Measure = Amount.Liquid

}

object Spirit extends Ingredients(Drinks) {

  override def title: String = "Spirits"

  override def description: Content = "The primary alcoholic component of most cocktails."

  case object Gin extends Ingredient(Spirit) with Spirit {

    override def title = "Gin"

    override def description: Content =
      "A liquor which derives its predominant flavour from juniper berries (Juniperus communis)."

  }

  case object Rum extends Ingredient(Spirit) with Spirit {

    override def title = "Rum"

    override def description: Content = "A distilled alcoholic beverage made from sugarcane byproducts, " +
      "such as molasses or honeys, or directly from sugarcane juice, by a process of fermentation and distillation."

  }

  case object Tequila extends Ingredient(Spirit) with Spirit {

    override def title = "Tequila"

    override def description: Content = "a regionally specific distilled beverage and type of alcoholic drink made " +
      "from the blue agave plant, primarily in the area surrounding the city of Tequila, 65 km (40 mi) northwest of " +
      "Guadalajara, and in the highlands (Los Altos) of the central western Mexican state of Jalisco."

  }

  case object Vodka extends Ingredient(Spirit) with Spirit {

    override def title = "Vodka"

    override def description: Content = "A distilled beverage composed primarily of water and ethanol, sometimes " +
      "with traces of impurities and flavorings."

  }

  sealed trait Whiskey extends Spirit {

    override def title = "Whiskey"

  }

  object Whiskey extends Ingredients(Spirit) {

    override def title: String = "Whiskey"

    override def description: Content = "A type of distilled alcoholic beverage made from fermented grain mash."

    case object Bourbon extends Ingredient(Whiskey) with Whiskey {

      override def title = "Bourbon Whiskey"

      override def description: Content = "A type of American whiskey, a barrel-aged distilled spirit made primarily " +
        "from corn."

    }

    case object Rye extends Ingredient(Whiskey) with Whiskey {

      override def title = "Rye Whiskey"

      override def description: Content = "A distilled alcoholic beverage made from fermented grain mash of at least " +
        "51 percent rye."

    }

  }

}