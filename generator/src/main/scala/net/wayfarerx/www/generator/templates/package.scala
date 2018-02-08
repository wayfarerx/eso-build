package net.wayfarerx.www.generator

package object templates {

  trait AllTemplates
    extends MetadataTemplates
      with PageTemplates
      with stylesheets.AllStylesheets {
    self: Website =>
  }

}
