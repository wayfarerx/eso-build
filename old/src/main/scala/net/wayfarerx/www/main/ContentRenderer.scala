package net.wayfarerx.www
package home

import cats.effect.IO

import fs2.Stream

trait ContentRenderer {

  /** The content rendering strategy. */
  implicit final val contentRenderer: Renderer[Content] = {

    case Text(content) =>
      Stream(content)

    case Emphasis(content, id, classes) =>
      enclose("em", id, classes)(content)

    case Strong(content, id, classes) =>
      enclose("strong", id, classes)(content)

    case Paragraph(content, id, classes) =>
      enclose("p", id, classes)(content)

    case Div(content, id, classes) =>
      enclose("div", id, classes)(content)

    case Link(href, content, id, classes) =>
      enclose("a", id, classes, "href" -> href)(content)

    case Image(src, alt, id, classes) =>
      tag("img", id, classes, "src" -> src, "alt" -> alt)

    case HorizontalRule(id, classes) =>
      tag("hr", id, classes)

    case Cite(content, id, classes) =>
      enclose("cite", id, classes)(content)

    case Quote(cite, content, id, classes) =>
      enclose("q", id, classes, cite.map("cite" -> _).toSeq: _*)(content)

    case Blockquote(cite, content, id, classes) =>
      enclose("blockquote", id, classes, cite.map("cite" -> _).toSeq: _*)(content)

    case Span(content, id, classes) =>
      enclose("span", id, classes)(content)

    case Ordered(content, id, classes) =>
      enclose("ol", id, classes)(content: _*)

    case Unordered(content, id, classes) =>
      enclose("ul", id, classes)(content: _*)

    case Item(content, id, classes) =>
      enclose("li", id, classes)(content)

    case Section(content, id, classes) =>
      enclose("section", id, classes)(content)

    case Header(content, id, classes) =>
      enclose("header", id, classes)(content)

    case Footer(content, id, classes) =>
      enclose("footer", id, classes)(content)

    case Heading(level, content, id, classes) =>
      enclose(s"h$level", id, classes)(content)

    case Sequence(content) =>
      Stream emits content flatMap contentRenderer.render

  }

  private def tag(
    tag: String,
    id: Option[String],
    classes: Vector[String],
    attributes: (String, String)*): Stream[IO, String] =
    emit(tag, id, classes, attributes.toVector, None)

  private def enclose(
    tag: String,
    id: Option[String],
    classes: Vector[String],
    attributes: (String, String)*)(
    content: Content*): Stream[IO, String] =
    emit(tag, id, classes, attributes.toVector, Some(Sequence(content.toVector)))

  private def emit(
    tag: String,
    id: Option[String],
    classes: Vector[String],
    attributes: Vector[(String, String)],
    enclosed: Option[Content]): Stream[IO, String] = {
    Stream(s"<$tag").covary[IO] ++
      Stream.emits(id.toSeq).map(escapeQuotes).map(i => s""" id="$i"""") ++ {
      if (classes.isEmpty) Stream.empty
      else Stream(""" class="""") ++ Stream.emits(classes).map(escapeQuotes).intersperse(" ") ++ Stream(""""""")
    } ++ Stream.emits(attributes.toSeq).map {
      case (k, v) => s""" $k="${escapeQuotes(v)}""""
    } ++ Stream(">") ++ {
      enclosed match {
        case Some(content) => content.render ++ Stream(s"</$tag>")
        case None => Stream.empty
      }
    }
  }

  private def escapeQuotes(str: String): String =
    str.replace(""""""", "&quot;")

}
