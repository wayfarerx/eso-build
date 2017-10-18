package net.wayfarerx.www

import java.io.PrintWriter

trait Renderer[T] {

  def render(writer: PrintWriter, value: T): Unit

}
