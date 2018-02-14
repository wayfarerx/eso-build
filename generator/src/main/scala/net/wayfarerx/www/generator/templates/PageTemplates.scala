package net.wayfarerx.www.generator
package templates

import scalacss.ScalatagsCss._
import scalatags.Text.short._

trait PageTemplates {
  self: stylesheets.ClassedStyles with MetadataTemplates with BannerTemplates with Website =>

  final def homePage(page: Page, metadata: Metadata, content: Frag*): Frag =
    outerPage(page, metadata, banner("wayfarerx.net", page.image, navigation = false, footer = true), content)

  final def outerPage(page: Page, metadata: Metadata, content: Frag*): Frag =
    html(
      head(pageMetadata(page, metadata)),
      body(css.rows)(content: _*)
    )

}
