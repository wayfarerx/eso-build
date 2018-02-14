package net.wayfarerx.www.generator
package pages

import scalatags.Text.short._

trait HomePages { self: templates.PageTemplates =>

  final val Home: Page = Page("", Vector()) { () =>
    homePage(Home, Metadata("wayfarerx.net"),
      p("Hello wayfarer!"),
      p("Hello wayfarer!"),
      p("Hello wayfarer!"),
      p("Hello wayfarer!"),
      p("Hello wayfarer!"),
      p("Hello wayfarer!"),
      p("Hello wayfarer!"),
      p("Hello wayfarer!"),
      p("Hello wayfarer!"))
  }

}
