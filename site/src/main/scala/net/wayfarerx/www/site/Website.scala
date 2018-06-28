/*
 * Website.scala
 *
 * Copyright 2018 wayfarerx <x@wayfarerx.net> (@thewayfarerx)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wayfarerx.www
package site

import model._

/**
 * Metadata about the website.
 */
final class Website extends Site[Article] {

  import Markup.Link

  /* The name of the site. */
  override val name: Name =
    Name("wayfarerx.net")

  /* The primary author of the site. */
  override val author: Author =
    Author(Name("wayfarerx"), Some("thewayfarerx"))

  /* The base URL for the site. */
  override val baseUrl: String =
    "https://wayfarerx.net"

  /* The stylesheet for the site. */
  override lazy val stylesheet: String =
    Stylesheet.renderStylesheet

  /** The links to external stylesheets. */
  def stylesheetLinks: Vector[Site.StyleSheetLink] = Vector(
    Site.StyleSheetLink("https://fonts.googleapis.com/css?family=Alegreya|Roboto"),
    Site.StyleSheetLink("https://use.fontawesome.com/releases/v5.1.0/css/all.css",
      integrity = Some("sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt"),
      crossorigin = Some("anonymous"))
  )

  /* The header image for this site. */
  override def headerImage: Option[Asset.Image.Single] =
    Some(Asset.Image.Single(Name("header")))

  /* The navigation pointers. */
  override lazy val navigation: Vector[Link.Internal[_ <: AnyRef]] = Vector(
    Link.Internal(Pointer[AnyRef](Location(Name("drinks"))), None, None,
      Markup(Markup.Emphasis(Markup.empty, classes = Vector("fas", "fa-cocktail")),
        Markup.Span(Markup(" Drinks"), classes = Vector("navLabel")))),
    Link.Internal(Pointer[AnyRef](Location(Name("games"))), None, None,
      Markup(Markup.Emphasis(Markup.empty, classes = Vector("fas", "fa-gamepad")),
        Markup.Span(Markup(" Games"), classes = Vector("navLabel")))),
    Link.Internal(Pointer[AnyRef](Location(Name("code"))), None, None,
      Markup(Markup.Emphasis(Markup.empty, classes = Vector("fas", "fa-code")),
        Markup.Span(Markup(" Code"), classes = Vector("navLabel")))),
    Link.Internal(Pointer[AnyRef](Location(Name("thoughts"))), None, None,
      Markup(Markup.Emphasis(Markup.empty, classes = Vector("fas", "fa-lightbulb")),
        Markup.Span(Markup(" Thoughts"), classes = Vector("navLabel"))))
  )

  /* The identities pointers. */
  override lazy val identities: Vector[Link.Resolved] = Vector(
    Link.Resolved("https://twitter.com/thewayfarerx", Some("Twitter"),
      Markup.Emphasis(Markup.empty, classes = Vector("fab", "fa-twitter"))),
    Link.Resolved("https://github.com/wayfarerx", Some("GitHub"),
      Markup.Emphasis(Markup.empty, classes = Vector("fab", "fa-github-alt"))),
    Link.Resolved("https://www.twitch.tv/wayfarer_x", Some("Twitch"),
      Markup.Emphasis(Markup.empty, classes = Vector("fab", "fa-twitch"))),
    Link.Resolved("https://www.youtube.com/channel/UCjx8a2ya26OK-lrcisYDQSw", Some("YouTube"),
      Markup.Emphasis(Markup.empty, classes = Vector("fab", "fa-youtube"))),
    Link.Resolved("mailto:x@wayfarerx.net", Some("Email"),
      Markup.Emphasis(Markup.empty, classes = Vector("fas", "fa-envelope")))
  )

  /* The statement at the end of every page. */
  override lazy val statement: Vector[Markup] = Vector(
    Markup.Paragraph(Markup(
      "Copyright (c) 2018 ",
      Markup.Link.Internal(Pointer[AnyRef](Location.Root), None, Some("wayfarerx.net"), Markup("wayfarerx.net")),
      ", all rights reserved. "
    )),
    Markup.Paragraph(Markup(
      "All content licenced for reuse under ",
      Markup.Link.Resolved(
        "https://creativecommons.org/licenses/by-nc-sa/4.0/",
        Some("Attribution-NonCommercial-ShareAlike 4.0 International"),
        Markup("CC BY-NC-SA 4.0")
      ),
      " unless otherwise specified."
    ))
  )

  /* The type hints for the site. */
  override lazy val hints: Site.Hints[Article] = Site.Hints(
    Site.Hints.Select("drinks") -> drinks.Hints
  )

}
