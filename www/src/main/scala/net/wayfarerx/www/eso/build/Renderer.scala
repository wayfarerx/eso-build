package net.wayfarerx.www
package eso.build

import java.io.PrintWriter


final class Renderer(writer: PrintWriter) {

  def render(
    build: Build,
    layout: String = "article",
    frontMatter: Map[String, String] = Map(),
    startLeft: Boolean = true): Unit = {
    // Write the front matter.
    writer.println("---")
    writer.println(s"layout: $layout")
    writer.println(s"title: ${build.title}")
    writer.println(s"author: ${build.author.name}")
    writer.println(s"twitter: ${build.author.twitter}")
    writer.println(s"headline: ${build.headline}")
    for ((k, v) <- frontMatter) writer.println(s"$k: $v")
    writer.println("---")
    writer.println()
    // Write the sections.
    /*
    var remaining = Vector(Section(build.headline,
      if (build.description.isEmpty) Content.Textual(None, build.description)
      else Content.Textual(Some(build.description.head), build.description.tail),
      Content.Image(build.image),
      build.sections) -> 1)
    var doLeft = startLeft
    while (remaining.nonEmpty) {
      val (section, depth) = remaining.head
      val next = if (doLeft) renderLeft(section, depth) else renderRight(section, depth)
      remaining = next.map(_ -> (depth + 1)) ++ remaining.tail
      doLeft = !doLeft
    }
    */
  }

  private def renderLeft(section: Section, depth: Int): Vector[Section] = {
    val h = s"h${Math.max(2, Math.min(6, depth))}"
    writer.println("""<div class="row">""")
    writer.println("""<div class="col-md-7">""")
    if (depth >= 2) writer.println(s"""<$h>${section.title}</$h>""")
    ???
    /*
    render(section.major)
    writer.println("</div")
    writer.println("""<div class="col-md-5">""")
    render(section.minor)
    writer.println("</div")
    writer.println("</div>")
    section.subsections
    */
  }

  private def renderRight(section: Section, depth: Int): Vector[Section] = {
    val h = s"h${Math.max(2, Math.min(6, depth))}"
    writer.println("""<div class="row">""")
    writer.println("""<div class="col-md-7 col-md-push-5">""")
    if (depth >= 2) writer.println(s"""<$h>${section.title}</$h>""")
    ???
    /*
    render(section.major)
    writer.println("</div")
    writer.println("""<div class="col-md-5 col-md-pull-7">""")
    render(section.minor)
    writer.println("</div")
    writer.println("</div>")
    section.subsections
    */
  }

  /*
  private def render(text: Text, lead: Boolean): Unit =
    if (lead) writer.println(s"""<p class="lead">${text}</p>""")
    else writer.println(s"<p>${text}</p>")

  private def render(content: Content): Unit = content match {
    case Content.Image(path) =>
      writer.println(s"""<img class="img-responsive center-block" src="$path" alt="???>""")
    case Content.Textual(lead, paragraphs) =>
      lead foreach (render(_, true))
      paragraphs foreach (render(_, false))
  }
  */

}
