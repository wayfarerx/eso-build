package net.wayfarerx.www.generator

trait Page {

  def name: String

  def children: Vector[Page]

  def apply(): String

}
