package net.wayfarerx.www
package drinks
package cocktails

import net.wayfarerx.www.drinks.ingredients._

object Manhattan extends Cocktail {

  override def title: String = "Manhattan"

  override def style: Style = Cocktail.Style.BeforeDinner

  override def description: String = "A before dinner cocktail made with rye whiskey and sweet vermouth."

  override def imageDescription: String = "A Manhattan."

  override def glass: Glass = Glass.Cocktail

  override def components: Vector[Component[Ingredient]] = Vector(
    Spirit.Whiskey.Rye(5.cl),
    Wine.Vermouth.Sweet(2.cl),
    Mixer.Bitters.Angostura(1.dash),
    Garnish.MaraschinoCherry(1.item)
  )

  override def tools: Vector[Tool] = Vector(
    Tool.MixingGlass,
    Tool.Ice,
    Tool.Spoon,
    Tool.Strainer
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
