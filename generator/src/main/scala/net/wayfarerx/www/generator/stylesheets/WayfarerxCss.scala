package net.wayfarerx.www.generator.stylesheets

import scalatags.Text.all._
import scalatags.stylesheet._

// FIXME
trait WayfarerxCss {

  override def toString: String = Style.styleSheetText

  object Style extends StyleSheet {
    initStyleSheet()

    override def customSheetName: Option[String] = Some("wx")

    val x = cls(
      backgroundColor := wxBackgroundColor
    )

  }

}