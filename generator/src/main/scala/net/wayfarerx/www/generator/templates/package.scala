package net.wayfarerx.www.generator

package object templates {

  trait AllTemplates
    extends MetadataTemplates
      with BannerTemplates
      with PageTemplates {
    self: stylesheets.ClassedStyles
      with stylesheets.InlineStyles
      with Website =>
  }

}
