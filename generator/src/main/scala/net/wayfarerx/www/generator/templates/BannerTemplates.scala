package net.wayfarerx.www.generator
package templates

import scalacss.ScalatagsCss._
import scalatags.Text.short._

trait BannerTemplates { self: stylesheets.ClassedStyles =>

  final def banner(
    title: String,
    image: Asset,
    navigation: Boolean = true,
    footer: Boolean = false
  ): Frag = div {
    div(css.columns)(
      div(css.rows)(
        title
      ),
      div(css.columns)(

      )
    )
  }

}
