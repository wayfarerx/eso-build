package net.wayfarerx.www.eso.build

import net.wayfarerx.www.Text

case class Build(
  title: String,
  author: Author,
  headline: String,
  image: String,
  description: Vector[Text],
  sections: Vector[Section])
