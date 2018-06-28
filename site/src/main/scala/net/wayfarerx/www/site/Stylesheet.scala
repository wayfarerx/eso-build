/*
 * Stylesheet.scala
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

import scalacss.DevDefaults._
import scalacss.internal.Attr
//import scalacss.ProdDefaults._

/**
 * Stylesheet for the entire site.
 */
object Stylesheet extends StyleSheet.Standalone {

  import dsl._

  private val smallMax = 768.px

  private val mediumMin = 769.px

  private val mediumMax = 1536.px

  private val largeMin = 1537.px

  private val `font-family` = Attr.real("font-family")

  // Basic settings.

  "*" - (
    boxSizing.inherit,
    &.before - boxSizing.inherit,
    &.after - boxSizing.inherit
  )

  "html" - (
    boxSizing.borderBox,
    padding.`0`,
    margin.`0`,
    minHeight(100.vh),
    maxWidth(100.%%),
    overflowX.hidden
  )

  "body" - (
    media.screen - (
      backgroundColor(c"#262216"),
      color(c"#928677")
    ),
    padding.`0`,
    margin.`0`,
    minHeight(100.vh),
    maxWidth(100.%%),
    overflowX.hidden,
    `font-family` := "'Roboto', sans-serif",
    fontSize(100.%%),
    display.flex,
    flexDirection.column
  )

  "header" - (
    `font-family` := "'Alegreya', serif",
    &("p") - (`font-family` := "'Roboto', sans-serif")
  )

  "a" - (
    textDecorationLine.none,
    &.link - color(c"#b0a18e"),
    &.visited - color(c"#a09382"),
    &.hover - color(c"#beb2a2"),
    &.active - color(c"#ccc3b7")
  )

  // Top-level elements.

  "header.nav" - (
    display.flex,
    flexDirection.column,
    &("nav") - (
      paddingTop(0.5.em),
      paddingLeft(0.5.em),
      paddingBottom(0.5.em),
      display.flex,
      justifyContent.spaceBetween,
      alignItems.center
    ),
    &("a.home") - (
      media.screen.maxWidth(smallMax) - fontSize(1.25.em),
      media.screen.minWidth(mediumMin) - fontSize(1.5.em),
      display.flex,
      alignItems.center
    ),
    &("ul") - (
      padding.`0`,
      margin.`0`,
      display.flex,
      listStyle := "none"
    ),
    &("li") - (
      paddingLeft(0.25.em),
      paddingRight(0.5.em),
      `font-family` := "'Roboto', sans-serif",
      display.flex
    ),
    &(".navLabel") - (media.screen.maxWidth(smallMax) - display.none)
  )

  "main" - (
    backgroundColor(c"#49412c"),
    paddingLeft(2.em),
    paddingRight(2.em),
    marginLeft(2.em),
    marginRight(2.em),
    display.flex,
    flexDirection.column,
    flexGrow(1),
    &("article") - (
      display.flex,
      flexDirection.column
    )
  )

  "footer.nav" - (
    padding(0.5.em),
    display.flex,
    flexDirection.column,
    justifyContent.center,
    &("nav") - (
      display.flex,
      flexDirection.column
    ),
    &("ul") - (
      padding.`0`,
      margin.`0`,
      listStyle := "none",
      display.flex,
      justifyContent.center
    ),
    &("li") - (
      paddingLeft(0.25.em),
      paddingRight(0.25.em),
      paddingBottom(0.5.em),
      display.flex
    ),
    &("p") - (
      paddingBottom(0.5.em),
      margin.`0`,
      textAlign.center
    )
  )

  // Headers.

  "header.title" - (
    &("h1") - textAlign.center,
    &("p") - textAlign.center
  )

  // Utilities.

  /** Renders the CSS. */
  def renderStylesheet: String = render

}
