package net.wayfarerx.www
package drinks

sealed trait Amount

object Amount {

  private def round(amount: Double, suffix: Option[String] = None): String = {
    val rounded = (amount * 4).round / 4.0
    val wholePart = rounded.floor.toLong
    val fractionalPart = rounded - wholePart
    val result = Math.abs(fractionalPart) match {
      case fp if fp < 0.125 => s"$wholePart"
      case fp if fp < 0.375 => s"$wholePart\u00BC"
      case fp if fp < 0.625 => s"$wholePart\u00BD"
      case fp if fp < 0.875 => s"$wholePart\u00BE"
      case _ => s"${wholePart + 1}"
    }
    suffix map (s => s"$result $s") getOrElse result
  }

  case class Liquid(centiliters: Double) extends Amount {

    override def toString: String =
      if (centiliters >= 0.25) round(centiliters, Some("cl"))
      else if (centiliters >= Dash * 4) "4 dashes"
      else if (centiliters >= Dash * 3) "3 dashes"
      else if (centiliters >= Dash * 2) "2 dashes"
      else "1 dash"

  }

  case class Solid(grams: Double) extends Amount {

    override def toString: String =
      round(grams, Some("gm"))

  }

  case class Units(count: Double) extends Amount {

    override def toString: String =
      round(count)

  }

}
