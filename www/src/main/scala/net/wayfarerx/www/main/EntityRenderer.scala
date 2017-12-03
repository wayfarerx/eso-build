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
      "title" -> entity.title,
      "description" -> entity.description.stripped
    ) ++ entity.image.map { image =>
      Map[String, Data](
        "image" -> image.src,
        "image-alt" -> image.alt
      )
    }.getOrElse(Map())
    val (frontMatter, content) = entity match {
      case landing: Landing =>
        Map("layout" -> Landing) -> Sequence(landing.content)
      case topic: Topic =>
        Map("layout" -> Topic) -> Sequence(topic.content)
      case subtopic: Subtopic =>
        Map("layout" -> Subtopic) -> Sequence(subtopic.content)
      case article: Article =>
        Map("layout" -> Article) -> Sequence(article.content)
    }
    Stream("---", NewLine) ++
      Structure(metadata ++ frontMatter).render ++
      Stream(NewLine, "---", NewLine) ++
      content.render
  }

}
