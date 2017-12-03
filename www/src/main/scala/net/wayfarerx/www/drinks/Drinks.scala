package net.wayfarerx.www
package drinks

object Drinks extends Topic {

  override def name: String = "drinks"

  override def title: String = "Drinks"

  override def description: Content = "Drinking is fun."

  override def imageDescription: Option[String] = None

  override def content: Vector[Content] = Vector()

}
