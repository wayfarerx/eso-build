package net.wayfarerx.www
package drinks
package cocktails

import ingredients._

object Manhattan extends Cocktail {

  override def displayName: String = title

  override def title: String = "Manhattan"

  override def description: String = "A before dinner cocktail made with rye whiskey and sweet vermouth."

  override protected def imageDescription: String = "A Manhattan."

  override def kind: Cocktail.Kind = Cocktail.Kind.BeforeDinner

  override def glass: glasses.Glass =
    glasses.CocktailGlass

  override def tools: Vector[drinks.tools.Tool] = Vector(
    drinks.tools.Ice,
    drinks.tools.MixingGlass,
    drinks.tools.MixingSpoon,
    drinks.tools.Strainer
  )

  override def components: Vector[Cocktail.Component[Ingredient]] = Vector(
    Spirit.Whiskey.Rye(5.cl),
    Wine.Vermouth.Sweet(2.cl),
    Mixer.Bitters.Angostura(1.dash),
    Garnish.MaraschinoCherry(1.item)
  )

  override def instructions: Vector[Content] = Vector(
    "Add the whiskey, vermouth and bitters to a mixing glass with ice and stir well.",
    "Strain into a chilled cocktail glass and garnish with the Maraschino cherry."
  )

  override def references: Vector[Link] =
    Vector(
      Link("http://iba-world.com/iba-official-cocktails/manhattan/",
        "Manhattan at International Bartenders Association": Content),
        Link("https://en.wikipedia.org/wiki/Manhattan_(cocktail)",
          "Manhattan at Wikipedia": Content))

}
