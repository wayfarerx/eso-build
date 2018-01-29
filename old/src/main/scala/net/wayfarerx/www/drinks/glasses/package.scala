package net.wayfarerx.www
package drinks

package object glasses {

  trait Glass extends Article {

    override def parent: Option[Composite] = Some(Glasses)

    override def author: Option[String] = None

    override def headline: Option[String] = None

    override def content: Vector[Content] = Vector(description)

  }

  object Glasses extends Topic {

    override def parent: Option[Composite] = Some(Drinks)

    override def displayName = "glasses"

    override def title = "Glasses"

    override def description = "Drinking is fun."

    override def children: Vector[Component] = Vector(CocktailGlass)

  }

}
