package net.wayfarerx.www.generator
package templates

import scalatags.Text.short._

trait PageTemplates { self: MetadataTemplates =>

  final def homePage(page: Page, metadata: Metadata, content: Frag): Frag =
    outerPage(page, metadata, content)

  final def outerPage(page: Page, metadata: Metadata, content: Frag): Frag =
    html(
      head(pageMetadata(page, metadata)),
      body(content)
    )

}
