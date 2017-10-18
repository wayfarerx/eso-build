package net.wayfarerx.eso.build

case class Build(
  title: String,
  author: Author,
  headline: String,
  image: String,
  description: Vector[Text],
  sections: Vector[Section])
