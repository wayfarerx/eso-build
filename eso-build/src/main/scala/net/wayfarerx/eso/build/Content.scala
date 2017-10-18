package net.wayfarerx.eso.build

sealed trait Content {

}

object Content {

  case class Image(path: String) extends Content

  case class Textual(lead: Option[Text], paragraphs: Vector[Text]) extends Content

}