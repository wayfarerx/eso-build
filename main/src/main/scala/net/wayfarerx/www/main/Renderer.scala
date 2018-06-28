/*
 * Renderer.scala
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
package main

import java.nio.file.{Files, StandardCopyOption => CopyOption, Path => JPath}

import util.{Success, Try}

import scalatags.Text.all._
import scalatags.Text.tags2.{article, main, nav, section}
import scalatags.Text.attrs.{id, cls}
import scalatags.Text.TypedTag

import model._

/**
 * A renderer for pages and assets in the site.
 *
 * @param site        The site that is being rendered.
 * @param source      The source directory.
 * @param destination The destination directory.
 */
final class Renderer(site: Site[_], source: JPath, destination: JPath) {

  import Renderer._

  /** The resolver to use. */
  private val resolver = new Resolver

  /**
   * Attempts to render a node to a HTML file.
   *
   * @param node   The node to render.
   * @param output The object to write output to.
   * @return The assets referenced by the rendered page.
   */
  def render(node: Node, output: Array[Byte] => Try[Unit]): Try[Set[(Location, String)]] = {
    node.encode() flatMap { document =>
      val (html, assets) = renderPage(node, document)
      output(html.getBytes(UTF8)) map (_ => assets)
    }

  }

  /**
   * Attempts to deploy all the specified assets.
   *
   * @param assets The assets to deploy.
   * @return The result of the asset deployment.
   */
  def deploy(assets: Set[(Location, String)]): Try[Unit] =
    ((Success(()): Try[Unit]) /: assets) { (state, asset) =>
      state map { _ =>
        val (location, file) = asset
        val (from, to) = location match {
          case Location.Root =>
            source -> destination
          case nested =>
            val path = nested.path.toString
            source.resolve(path) -> destination.resolve(path)
        }
        Files.createDirectories(to)
        Files.copy(from.resolve(file), to.resolve(file), CopyOption.REPLACE_EXISTING)
      }
    }

  /**
   * Renders a HTML page for the specified node's encoded document.
   *
   * @param node     The node to render a HTML page for.
   * @param document The document to render.
   * @return The rendered HTML page and the assets referenced by the rendered page.
   */
  private def renderPage(node: Node, document: Document): (String, Set[(Location, String)]) = {
    var assetsToDeploy = Set[(Location, String)]()

    /* Render the <head> section and all metadata. */
    def renderHead: TypedTag[String] = {
      val title =
        if (document.title == site.name.display) site.name.display
        else s"${document.title} - ${site.name.display}"
      head(
        meta(charset := UTF8.toLowerCase),
        meta(httpEquiv := "X-UA-Compatible", content := "IE=edge"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1"),
        scalatags.Text.tags2.title(title),
        meta(name := "description", content := document.description.strip),
        meta(name := "keywords", content := document.description.strip),
        meta(name := "og:title", content := document.title),
        meta(name := "og:description", content := document.description.strip),
        meta(name := "og:site_name", content := site.name.display),
        meta(name := "og:url", content := node.site.baseUrl + node.location),
        resolver.resolve(node, document.image) map { image =>
          assetsToDeploy += image.resource
          val (location, file) = image.resource
          frag(
            meta(name := "og:image", content := node.site.baseUrl + location + file),
            image.alt map (alt => frag(meta(name := "og:image:alt", content := alt))) getOrElse frag()
          )
        } getOrElse frag(),
        meta(name := "twitter:card", content := "summary_large_image"),
        site.author.twitter.map(t => meta(name := "twitter:site", content := s"@$t")) getOrElse frag(),
        document.author.flatMap(_.twitter).orElse(site.author.twitter).map { t =>
          meta(name := "twitter:creator", content := s"@$t")
        } getOrElse frag(),
        link(rel := "stylesheet", href := "/css/stylesheet.css"),
        site.stylesheetLinks map {
          case Site.StyleSheetLink(_href, None, None) =>
            link(rel := "stylesheet", href := _href)
          case Site.StyleSheetLink(_href, Some(integrity), None) =>
            link(rel := "stylesheet", href := _href, attr("integrity") := integrity)
          case Site.StyleSheetLink(_href, None, Some(crossorigin)) =>
            link(rel := "stylesheet", href := _href, attr("crossorigin") := crossorigin)
          case Site.StyleSheetLink(_href, Some(integrity), Some(crossorigin)) =>
            link(rel := "stylesheet", href := _href, attr("integrity") := integrity, attr("crossorigin") := crossorigin)
        }
      )
    }

    /* Renders the document header. */
    def renderHeader: TypedTag[String] =
      header(`class` := "nav")(
        nav(
          a(href := site.baseUrl + "/", title := site.name.display, `class` := "home")(
            site.headerImage flatMap (resolver.resolve(node, _) map { image =>
              val _src = image.resource._1 + image.resource._2
              image.alt map (a => img(src := _src, alt := a)) getOrElse img(src := _src)
            }) getOrElse frag(),
            site.name.display
          ),
          ul(site.navigation map { link =>
            li(renderInternalLink(link, link.pointer, link.fragment, link.title, link.nested))
          })
        )
      )

    /* Renders the document article. */
    def renderArticle: TypedTag[String] = {
      val description = renderMarkup(document.description)
      article(
        header(id := Name(document.title).value, cls := "title",
          h1(document.title),
          p(description),
          document.author map { author =>
            frag(author.twitter map { twitter =>
              a(href := s"https://twitter.com/$twitter", author.name.display)
            } getOrElse author.name.display)
          } getOrElse frag()
        ),
        renderMarkup(document.content),
        renderSections(document.sections)
      )
    }

    /* Renders the document footer. */
    def renderFooter: TypedTag[String] =
      footer(`class` := "nav")(
        nav(
          ul(site.identities map (m => li(renderMarkup(m))))
        ),
        site.statement.map(renderMarkup)
      )

    /* Render the specified sections. */
    def renderSections(sections: Vector[Section]): Frag = frag(sections map { s =>
      renderTag(s, section(_: _*))(
        header(id := Name(s.header.strip).value, s.level match {
          case 1 => h1(renderMarkup(s.header))
          case 2 => h2(renderMarkup(s.header))
          case 3 => h3(renderMarkup(s.header))
          case 4 => h4(renderMarkup(s.header))
          case 5 => h5(renderMarkup(s.header))
          case 6 => h6(renderMarkup(s.header))
          case _ => frag()
        }),
        renderMarkup(s.content),
        renderSections(s.sections))
    }: _*)

    /* Render the specified markup. */
    def renderMarkup(markup: Markup): Frag = markup match {

      case Markup.Text(text) =>
        text

      case tag@Markup.Span(nested, _title, _, _) =>
        renderTag(tag, attrs => span(_title.map(title := _).toVector ++: attrs: _*)(renderMarkup(nested)))

      case tag@Markup.Emphasis(nested, _, _) =>
        renderTag(tag, em(_: _*)(renderMarkup(nested)))

      case tag@Markup.Strong(nested, _, _) =>
        renderTag(tag, strong(_: _*)(renderMarkup(nested)))

      case tag@Markup.Paragraph(nested, _, _) =>
        renderTag(tag, p(_: _*))(renderMarkup(nested))

      case tag@Markup.Figure(image, text, _, _) =>
        resolver.resolve(node, image) map { resolved =>
          assetsToDeploy += resolved.resource
          val _src = s"${resolved.resource._1}${resolved.resource._2}"
          renderTag(tag, figure(_: _*))(
            resolved.alt map (_alt => img(src := _src, alt := _alt)) getOrElse img(src := _src),
            if (text == Markup.empty) frag() else figcaption(renderMarkup(text))
          )
        } getOrElse frag()

      case tag@Markup.Link.Local(_name, _title, nested, _, _) =>
        renderTag(tag, s => a(
          (href := "#" + _name) +: _title.map(title := _).toSeq ++: s
        )(renderMarkup(nested)))

      case tag@Markup.Link.Internal(pointer, fragment, _title, nested, _, _) =>
        renderInternalLink(tag, pointer, fragment, _title, nested)

      case tag@Markup.Link.Resolved(_href, _title, nested, _, _) =>
        renderTag(tag, s => a(
          (href := rebase(_href)) +: _title.map(title := _).toSeq ++: s
        )(renderMarkup(nested)))

      case tag@Markup.List.Ordered(items, _, _) =>
        renderTag(tag, ol(_: _*))(frag(items map renderMarkup))

      case tag@Markup.List.Unordered(items, _, _) =>
        renderTag(tag, ul(_: _*))(frag(items map renderMarkup))

      case tag@Markup.List.Item(nested, _, _) =>
        renderTag(tag, li(_: _*))(renderMarkup(nested))

      case Markup.Sequence(nested) =>
        frag(nested map renderMarkup: _*)

    }

    /* Render a tag with style information. */
    def renderTag(target: Styled, f: (Seq[Modifier]) => TypedTag[String]): TypedTag[String] = f {
      target.id.map(id := _).toSeq ++ (target.classes match {
        case Vector() => Seq()
        case classes => Seq(cls := classes.mkString(" "))
      })
    }

    /* Render a link to an internal resource. */
    def renderInternalLink(
      tag: Styled,
      pointer: Pointer[_ <: AnyRef],
      fragmentOpt: Option[Name],
      titleOpt: Option[String],
      nested: Markup
    ): TypedTag[String] = {
      val fragment = fragmentOpt map ("#" + _.value) getOrElse ""
      node.context.locate(pointer).flatMap { resolved =>
        node.context.loadTitle(pointer) map { _title =>
          renderTag(tag, s => a(
            (href := rebase(resolved) + fragment) +:
              titleOpt.orElse(node.context.loadDescription(pointer).map(_.strip).toOption)
                .map(t => title := s"${_title}: ${decapitalize(t)}") ++: s
          )(renderMarkup(nested)))
        }
      }.get
    }

    /* Convert nested entity references to relative paths. */
    def rebase(location: String): String = {
      val base = node.location.toString
      if (location startsWith base) location substring base.length else location
    }

    // Render the page.
    "<!DOCTYPE html>" + html(
      renderHead,
      body(
        renderHeader,
        main(renderArticle),
        renderFooter
      )
    ).render -> assetsToDeploy
  }

}

/**
 * Definitions associated with renderers.
 */
object Renderer {

  /** The codec to use. */
  private val UTF8 = "UTF-8"

  /**
   * Removes the initial character's capitalization from relevant strings.
   *
   * @param str The string to decapitalize.
   * @return The decapitalized string.
   */
  private def decapitalize(str: String): String = {
    if (str.isEmpty) str else str.take(2) match {
      case string if string.length == 2 && (string(1).isLetter && string(1).isUpper) || string.length < 2 => string
      case string => string.toLowerCase + str.substring(2)
    }
  }

}