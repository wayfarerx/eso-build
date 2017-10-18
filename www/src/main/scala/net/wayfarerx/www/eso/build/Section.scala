package net.wayfarerx.www.eso.build

case class Section(
  title: String,
  major: Content,
  minor: Content,
  subsections: Vector[Section] = Vector())
