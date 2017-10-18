package net.wayfarerx.www

import java.io.PrintWriter

sealed trait Text {

  def ++(that: Text): Text

  def isEmpty: Boolean

}

object Text {

  implicit val TextRenderer: Renderer[Text] = { (writer: PrintWriter, text: Text) =>
    if (!text.isEmpty) text match {
      case Raw(content) =>
        writer.write(content)
      case Span(content, id, classes) =>
        writer.write("<span")
        id foreach (i => writer.write(s""" id="$i""""))
        if (classes.nonEmpty) {
          writer.write(""" class="""")
          var first = true
          classes foreach { c =>
            if (first) first = false
            else writer.write(' ')
            writer.write(c)
          }
          writer.write('"')
        }
        writer.write('>')
        TextRenderer.render(writer, content)
        writer.write("</span>")
      case Multiple(content) =>
        content foreach (TextRenderer.render(writer, _))
    }
  }

  def apply(content: String): Text =
    Raw(content)

  def apply(content: Text, mainClass: String, otherClasses: String*): Text =
    Span(content, None, mainClass +: otherClasses: _*)

  def apply(content: Text, id: Option[String], classes: String*): Text =
    Span(content, id, classes: _*)

  def apply(content: Text*): Text =
    Multiple(content.toVector)

  case class Raw(content: String) extends Text {

    override def isEmpty: Boolean = false

    override def ++(that: Text): Text = that match {
      case Multiple(them) => Multiple(this +: them)
      case other => Multiple(this, other)
    }

  }

  case class Span(content: Text, id: Option[String] = None, classes: Vector[String] = Vector()) extends Text {

    override lazy val isEmpty: Boolean =
      content.isEmpty

    override def ++(that: Text): Text = that match {
      case Multiple(them) => Multiple(this +: them)
      case other => Multiple(this, other)
    }

  }

  object Span {

    def apply(content: Text, id: Option[String], classes: String*): Span =
      Span(content, id, classes.toVector)

  }

  case class Multiple(content: Vector[Text]) extends Text {

    override lazy val isEmpty: Boolean =
      content exists (!_.isEmpty)

    override def ++(that: Text): Text = that match {
      case Multiple(them) => Multiple(content ++ them)
      case other => Multiple(content :+ other)
    }

  }

  object Multiple {

    def apply(content: Text*): Multiple =
      Multiple(content.toVector)

  }

}
