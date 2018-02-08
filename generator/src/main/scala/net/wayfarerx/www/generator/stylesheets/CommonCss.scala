package net.wayfarerx.www.generator.stylesheets

import scalatags.Text.all._
import scalatags.stylesheet._

// FIXME
trait CommonCss {

  private val flexColumn: Vector[StyleSheetFrag] = Vector(
    display.flex,
    flexDirection.column
  )

  private val flexRow: Vector[StyleSheetFrag] = Vector(
    display.flex,
    flexDirection.row
  )

  object CommonStyles extends StyleSheet {
    initStyleSheet()

    override def customSheetName: Option[String] = Some("wx")

    val column = cls(flexColumn: _*)

    val row = cls(flexRow: _*)

    override def toString: String = {
      def fix(text: String): String =
        text.indexOf('{') match {
          case bracket if bracket > 0 && !Character.isWhitespace(text.charAt(bracket - 1)) =>
            s"${text.substring(0, bracket)} {${fix(text.substring(bracket + 1, text.length))}"
          case bracket if bracket == 0 =>
            "{" + fix(text.substring(1))
          case _ =>
            text
        }

      fix(styleSheetText)
    }

  }

}