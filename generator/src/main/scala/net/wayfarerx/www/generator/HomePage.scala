package net.wayfarerx.www.generator

import scalatags.Text.all._

object HomePage extends Page {

  override def name: String = ""

  override def children: Vector[Page] = Vector()

  override def apply(): String =
    html {

    }.render

}
