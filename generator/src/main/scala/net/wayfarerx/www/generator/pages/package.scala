package net.wayfarerx.www.generator

import language.implicitConversions

import scalatags.Text.short._

package object pages {

  implicit def renderHtml(html: Frag): String = "<!DOCTYPE html>\r\n" + html.render

  trait AllPages
    extends HomePages {
    self: templates.PageTemplates =>
  }

}
