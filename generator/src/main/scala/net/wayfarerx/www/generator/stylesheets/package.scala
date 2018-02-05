package net.wayfarerx.www.generator

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

  val wxBackgroundColorFade: String = ".3"

  val HeadlineFont: String = "normal normal normal 1em 'IM Fell Great Primer', serif"
  val wxLeadFont: String = "normal normal bold 1em 'Raleway', sans-serif"
  val wxCopyFont: String = "normal normal normal 1em 'Raleway', sans-serif"

  val wxSmallMaxSize: String = " 768px"
  val wxMediumMinSize: String = " 769px"
  val wxMediumMaxSize: String = " 1536px"
  val wxLargeMinSize: String = " 1537px"

  trait AllStylesheets
    extends CommonCss
    with WayfarerxCss

}
