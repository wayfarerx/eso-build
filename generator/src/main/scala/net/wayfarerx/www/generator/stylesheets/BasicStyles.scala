package net.wayfarerx.www.generator
package stylesheets

import scalacss.DevDefaults._

trait BasicStyles extends Styles {

  object BasicCss extends StyleSheet.Standalone {

    import dsl._

    private def htmlOrBody = mixin(
      border.`0`,
      margin.`0`,
      padding.`0`,
      maxWidth(100.%%),
      overflowX.hidden
    )

    "*" - (
      boxSizing.inherit,
      &.before - boxSizing.inherit,
      &.after - boxSizing.inherit
    )

    "html" - (
      boxSizing.borderBox,
      htmlOrBody
    )

    "body" - (
      backgroundColor(Color(wxBackgroundColor)),
      color(Color(wxTextColor)),
      font := wxCopyFont,
      htmlOrBody,
      &.after - (
        display.block,
        position.fixed,
        top.`0`,
        left.`0`,
        width(100.vw),
        height(100.vh),
        zIndex(-1),
        content := "' '",
        backgroundAttachment := "fixed",
        backgroundPosition := "center",
        backgroundRepeat := "no-repeat",
        backgroundSize := "cover"
      )
    )

    "a" - (
      textDecoration := "none",
      &.link - color(Color(wxLinkColor)),
      &.visited - color(Color(wxVisitedColor)),
      &.hover - color(Color(wxHoverColor)),
      &.active - color(Color(wxActiveColor))
    )

  }

}
