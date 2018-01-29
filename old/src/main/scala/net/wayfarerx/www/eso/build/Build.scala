package net.wayfarerx.www.eso.build

import net.wayfarerx.www.Content

case class Build(
  title: String,
  author: Author,
  headline: String,
  image: String,
  description: Vector[Content],
  sections: Vector[Section])
