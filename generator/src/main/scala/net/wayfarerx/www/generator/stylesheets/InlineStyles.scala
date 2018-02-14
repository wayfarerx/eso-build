package net.wayfarerx.www.generator
package stylesheets

import scalacss.DevDefaults._

trait InlineStyles extends Styles {
  self: Website =>

  class InlineCss(backgrounds: Backgrounds) extends StyleSheet.Standalone with Styles {

    import dsl._

    "body".after - (
      media.screen (
        backgroundImage := s"url('${backgrounds.large.location}')"),
      media.screen.maxWidth(wxMediumMaxSize.px).maxHeight(wxMediumMaxSize.px)(
        backgroundImage := s"url('${backgrounds.medium.location}')"),
      media.screen.maxWidth(wxSmallMaxSize.px).maxHeight(wxSmallMaxSize.px)(
        backgroundImage := s"url('${backgrounds.small.location}')")
    )

  }

}
