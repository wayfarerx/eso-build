package net.wayfarerx.www.generator

package object templates {

  trait AllTemplates
    extends MetadataTemplates
      with PageTemplates {
    self: Website =>
  }

}
