package net.wayfarerx.www.generator
package stylesheets

import scalacss.DevDefaults._
import scalacss.internal.ClassNameHint

trait ClassedStyles extends Styles {

  val css: ClassedCss.type = ClassedCss

  object ClassedCss extends StyleSheet.Inline {

    import dsl._

    override protected val classNameHint: ClassNameHint =
      ClassNameHint("wx")

    private def landscapeSmall = media.landscape.minWidth(wxLargeMinSize.px)

    private def landscapeMedium = media.landscape.minWidth(wxMediumMinSize.px).maxWidth(wxMediumMaxSize.px)

    private def landscapeLarge = media.landscape.maxWidth(wxSmallMaxSize.px)

    private def portraitSmall = media.portrait.minWidth(wxLargeMinSize.px)

    private def portraitMedium = media.portrait.minWidth(wxMediumMinSize.px).maxWidth(wxMediumMaxSize.px)

    private def portraitLarge = media.portrait.maxWidth(wxSmallMaxSize.px)


    val rows = style(display.flex, flexDirection.column)

    val columns = style(display.flex, flexDirection.row)

    val justified = style(justifyContent.spaceBetween)

  }

}
