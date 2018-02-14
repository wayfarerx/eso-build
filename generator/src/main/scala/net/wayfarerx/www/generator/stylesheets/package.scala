package net.wayfarerx.www.generator

import scalacss.DevDefaults._

package object stylesheets {

  trait Styles {

    def wxBackgroundColor: String = "#262216"

    def wxContainerColor: String = "#49412c"

    def wxHighlightColor: String = "#97743a"

    def wxLinkColor: String = "#b0a18e"

    def wxHoverColor: String = "#beb2a2"

    def wxActiveColor: String = "#ccc3b7"

    def wxVisitedColor: String = "#a09382"

    def wxTextColor: String = "#928677"

    def wxShadowColor: String = "#000000"

    def wxFeatureColor: String = "#0a260a"

    def wxHeadlineFont: String = "normal normal normal 1em 'IM Fell Great Primer', serif"

    def wxLeadFont: String = "normal normal bold 1em 'Raleway', sans-serif"

    def wxCopyFont: String = "normal normal normal 1em 'Raleway', sans-serif"

    def wxSmallMaxSize: Int = 768

    def wxMediumMinSize: Int = wxSmallMaxSize + 1

    def wxMediumMaxSize: Int = 1536

    def wxLargeMinSize: Int = wxMediumMaxSize + 1

    def wxBodyPaddingSmall: Int = 3

    def wxBodyPaddingMedium: Int = 5

    def wxBodyPaddingLarge: Int = 7

  }

  trait AllStyles extends BasicStyles with ClassedStyles with InlineStyles { self: Website =>

    def masterStylesheet: String =
      s"""@import url('https://fonts.googleapis.com/css?family=IM+Fell+Great+Primer|Raleway');
         |
         |${BasicCss.render}
         |${ClassedCss.render[String]}
         |""".stripMargin

  }


}
