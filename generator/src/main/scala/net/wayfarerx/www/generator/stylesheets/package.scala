package net.wayfarerx.www.generator

import scalatags.Text.all._
import scalatags.stylesheet._

package object stylesheets {

  val wxBackgroundColor: String = "#262216"
  val wxContainerColor: String = "#49412c"
  val wxHighlightColor: String = "#97743a"

  val wxLinkColor: String = "#b0a18e"
  val wxHoverColor: String = "#beb2a2"
  val wxActiveColor: String = "#ccc3b7"
  val wxVisitedColor: String = "#a09382"

  val wxTextColor: String = "#928677"
  val wxShadowColor: String = "#000000"
  val wxFeatureColor: String = "#0a260a"

  val HeadlineFont: String = "normal normal normal 1em 'IM Fell Great Primer', serif"
  val wxLeadFont: String = "normal normal bold 1em 'Raleway', sans-serif"
  val wxCopyFont: String = "normal normal normal 1em 'Raleway', sans-serif"

  val wxSmallMaxSize: Int = 768
  val wxMediumMinSize: Int = wxSmallMaxSize + 1
  val wxMediumMaxSize: Int = 1536
  val wxLargeMinSize: Int = wxMediumMaxSize + 1

  trait AllStylesheets
    extends BasicCss
      with CommonCss
      with BannersCss {

    def masterStylesheet: String =
      s"""@media screen {
         |${indent(BasicStyles.toString)}
         |${indent(CommonStyles.toString)}
         |${indent(BannerStyles.toString)}
        |}""".stripMargin

    private def indent(lines: String): String =
      io.Source.fromString(lines).getLines()  filter (_.nonEmpty) map ("  " + _) mkString "\r\n"

  }

}
