package net.wayfarerx.www
package main

import cats.effect.IO

import fs2.Stream

object EntityRenderer extends Renderer[Entity] {

  val Landing: Data = "landing"
  val Topic: Data = "topic"
  val Subtopic: Data = "subtopic"
  val Article: Data = "article"

  override def render(entity: Entity): Stream[IO, String] = {
    val metadata = Map[String, Data](
      "id" -> entity.id,
      "title" -> entity.title,
      "description" -> entity.description.stripped,
      "image" -> entity.image.src,
      "image-alt" -> entity.image.alt
    )
    val (frontMatter, content) = entity match {
      case landing: Landing =>
        Map("layout" -> Landing) ->
          Sequence(Vector(Image("/images/home.png", "wayfarerx logo", "landing"))) // FIXME
      case topic: Topic =>
        Map("layout" -> Topic) ++
          topic.headline.map(h => Map("headline" -> (s"'${h.replace("'", "''")}'": Data))).getOrElse(Map()) ->
          componentsToContent(topic.components)
      case subtopic: Subtopic =>
        Map("layout" -> Topic) ->
          componentsToContent(subtopic.components)
      case article: Article =>
        Map("layout" -> Article) ->
          Sequence(article.content)
    }
    Stream("---", NewLine) ++
      Structure(metadata ++ frontMatter).render ++
      Stream(NewLine, "---", NewLine) ++
      content.render
  }

  private def componentsToContent(components: Vector[Component]): Sequence = Sequence(components map { component =>
    Section(Header(Heading(2, component.title)) ~ Paragraph(component.description) ~ component.image) ~ NewLine
  })

}
