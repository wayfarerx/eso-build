package net.wayfarerx.tools

object Main extends App {

  type Vector = (Double, Double)

  val LightingMin: Double = 66

  val LightingMax: Double = 128

  val LightingRange: Double = LightingMax - LightingMin

  val Lighting: Vector = (1, 2)

  System.exit {
    if (args.length == 0) printHelp() else args(0) match {
      case c if c.equalsIgnoreCase("lighting") && args.length == 2 =>
        lighting((args(1).toDouble, args(2).toDouble))
      case _ =>
        printHelp()
    }
  }

  def lighting(normal: Vector): Int = {

    0 // FIXME
  }

  def printHelp(): Int = {
    println("Usage: tools command ...")
    println("  tools lighting <x-normal> <y-normal>")
    1
  }

}
