package net.wayfarerx.www
package drinks
package ingredients

sealed trait Spirit extends Ingredient {

  final override type Measure = Amount.Liquid

  override def parent: Composite = Spirits

}

object Spirit {

  case object Gin extends Spirit {

    override def title = "Gin"

    override def description: String =
      "A liquor which derives its predominant flavour from juniper berries (Juniperus communis)."

  }

  case object Rum extends Spirit {

    override def title = "Rum"

    override def description: String = "A distilled alcoholic beverage made from sugarcane byproducts, " +
      "such as molasses or honeys, or directly from sugarcane juice, by a process of fermentation and distillation."

  }

  case object Tequila extends Spirit {

    override def title = "Tequila"

    override def description: String = "a regionally specific distilled beverage and type of alcoholic drink made " +
      "from the blue agave plant, primarily in the area surrounding the city of Tequila, 65 km (40 mi) northwest of " +
      "Guadalajara, and in the highlands (Los Altos) of the central western Mexican state of Jalisco."

  }

  case object Vodka extends Spirit {

    override def title = "Vodka"

    override def description: String = "A distilled beverage composed primarily of water and ethanol, sometimes " +
      "with traces of impurities and flavorings."

  }

  sealed abstract class Whiskey extends Spirit {

    override def parent: Composite = Whiskeys

  }

  object Whiskey {

    case object Bourbon extends Whiskey {

      override def name: String = "bourbon"

      override def title = "Bourbon Whiskey"

      override def description: String = "A type of American whiskey, a barrel-aged distilled spirit made primarily " +
        "from corn."

    }

    case object Rye extends Whiskey {

      override def name: String = "rye"

      override def title = "Rye Whiskey"

      override def description: String = "A distilled alcoholic beverage made from fermented grain mash of at least " +
        "51 percent rye."

    }

  }

  object Whiskeys extends Ingredients {

    override def parent: Composite = Spirits

    override def name: String = "whiskey"

    override def title: String = "Whiskey"

    override def image: Image = parent.image

    override def description: String = "A type of distilled alcoholic beverage made from fermented grain mash."

    override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

    override def components: Vector[Component] = Vector(Whiskey.Bourbon, Whiskey.Rye)

  }

}

object Spirits extends Ingredients {

  override def parent: Composite = Drinks

  override def name: String = "spirits"

  override def title: String = "Spirits"

  override def image: Image = parent.image

  override def description: String = "Drinking is fun."

  override def headline: Option[String] = Some(""""Whisky is liquid sunshine." - George Bernard Shaw""")

}
