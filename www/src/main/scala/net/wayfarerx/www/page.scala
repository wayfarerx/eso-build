/*
 * page.scala
 *
 * Copyright 2017 wayfarerx <x@wayfarerx.net> (@thewayfarerx)
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

/**
 * Base class for all generated pages.
 */
sealed trait Page {

  /** The title of this page. */
  def title: String

  /** The category this page is in. */
  def category: String

  /** The description of this page. */
  def description: String

  /** The optional image for this page. */
  def image: Option[Image]

  /** The metadata for this page. */
  def metadata: Map[String, Data] = Map()

}

/**
 * An article that presents a single idea or story.
 *
 * @param title The title of this article.
 * @param description The description of this article.
 * @param category The category this article is in.
 * @param image The optional image for this article.
 * @param headline The headline for this article.
 * @param twitter The author of this article's Twitter handle.
 * @param content The content of this article.
 * @param references The references in this article.
 */
case class Article(
  title: String,
  description: String,
  category: String,
  image: Option[Image],
  headline: String,
  twitter: Option[String],
  content: Vector[Content],
  references: Vector[(String, String)]
) extends Page {

  /* Consists of the layout and article-specific properties. */
  override def metadata: Map[String, Data] =
    Map[String, Data]("layout" -> "article", "headline" -> headline) ++
      (twitter.map(t => Map[String, Data]("twitter" -> t)) getOrElse Map()) ++
      (if (references.isEmpty) Map() else
        Map("references" -> Collection(references map (r => Structure("text" -> r._1, "link" -> r._2)))))

}
