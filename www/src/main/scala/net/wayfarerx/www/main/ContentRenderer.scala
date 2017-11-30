package net.wayfarerx.www
package main

import cats.effect.IO

import fs2.Stream

object ContentRenderer extends Renderer[Content] {

  override def render(value: Content): Stream[IO, String] = value match {

    case Text(content) =>
      Stream(content)

    case Link(href, content, id, classes) =>
      open("a", id, classes, "href" -> href) ++ render(content) ++ close("a")

    case Image(src, alt, id, classes) =>
      open("img", id, classes, "src" -> src, "alt" -> alt)

    case Cite(content, id, classes) =>
      open("cite", id, classes) ++ render(content) ++ close("cite")

    case Quote(cite, content, id, classes) =>
      open("q", id, classes, cite.map("cite" -> _).toSeq: _*) ++ render(content) ++ close("q")

    case Blockquote(cite, content, id, classes) =>
      open("blockquote", id, classes, cite.map("cite" -> _).toSeq: _*) ++ render(content) ++ close("blockquote")

    case Span(content, id, classes) =>
      open("span", id, classes) ++ render(content) ++ close("span")

    case Ordered(content, id, classes) =>
      open("ol", id, classes) ++ Stream.emits(content).flatMap(render(_)) ++ close("ol")

    case Unordered(content, id, classes) =>
      open("ul", id, classes) ++ Stream.emits(content).flatMap(render(_)) ++ close("ul")

    case Item(content, id, classes) =>
      open("li", id, classes) ++ render(content) ++ close("li")

    case Sequence(content) =>
      Stream.emits(content).flatMap(render(_))

  }

  private def open(
    tag: String,
    id: Option[String],
    classes: Vector[String],
    attributes: (String, String)*): Stream[IO, String] =
    Stream(s"<$tag") ++
      Stream.emits(id.toSeq).map(escapeQuotes).map(i => s""" id="$i"""") ++ {
      if (classes.isEmpty) Stream.empty
      else Stream(""" class="""") ++ Stream.emits(classes).map(escapeQuotes).intersperse(" ") ++ Stream(""""""")
    } ++ Stream.emits(attributes.toSeq).map {
      case (k, v) => s""" $k="${escapeQuotes(v)}""""
    } ++ Stream(">")

  private def close(tag: String): Stream[IO, String] =
    Stream(s"</$tag>")

  private def escapeQuotes(str: String): String =
    str.replace(""""""", "&quot;")

}
