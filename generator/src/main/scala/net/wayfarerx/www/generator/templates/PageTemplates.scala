package net.wayfarerx.www.generator
package templates

import scalatags.Text.short._

import stylesheets.CommonCss

trait PageTemplates { self: MetadataTemplates with CommonCss =>

  final def homePage(page: Page, metadata: Metadata, content: Frag): Frag =
    outerPage(page, metadata, content)

  final def outerPage(page: Page, metadata: Metadata, content: Frag): Frag =
    html(
      head(pageMetadata(page, metadata)),
      body(CommonStyles.column, content)
    )

}
