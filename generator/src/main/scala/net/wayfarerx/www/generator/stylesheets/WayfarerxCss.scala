package net.wayfarerx.www.generator.stylesheets

import scalatags.Text.all._
import scalatags.stylesheet._

// FIXME
trait WayfarerxCss {

  object WayfarerxStyles extends StyleSheet {
    initStyleSheet()

    override def customSheetName: Option[String] = Some("wx")

    val columns = cls(
      flexDisplay,
      flexDirection := "column"
    )

    override def toString: String = WayfarerxStyles.styleSheetText

  }

}