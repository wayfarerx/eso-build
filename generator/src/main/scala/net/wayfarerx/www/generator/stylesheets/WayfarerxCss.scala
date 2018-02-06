package net.wayfarerx.www.generator.stylesheets

import scalatags.Text.all._
import scalatags.stylesheet._

// FIXME
trait WayfarerxCss {

  object WayfarerxStyles extends StyleSheet {
    initStyleSheet()

    override def customSheetName: Option[String] = Some("wx")

    val x = cls(
      backgroundColor := wxBackgroundColor
    )

    override def toString: String = WayfarerxStyles.styleSheetText

  }

}