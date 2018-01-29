package net.wayfarerx.www
package drinks

package object tools {

  trait Tool extends Article {

    override def parent: Option[Composite] = Some(Tools)

    override def displayName: String = title.toLowerCase

    override def author: Option[String] = None

    override def headline: Option[String] = None

    override def content: Vector[Content] = Vector(description)

  }

  object Tools extends Topic {

    override def parent: Option[Composite] = Some(Drinks)

    override def displayName: String = title.toLowerCase

    override def title = "Tools"

    override def description = "Drinking is fun."

    override def children: Vector[Component] = Vector(Ice, MixingGlass, MixingSpoon, Strainer)

  }

}
