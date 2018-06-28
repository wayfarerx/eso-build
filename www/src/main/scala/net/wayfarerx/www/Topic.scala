/*
 * Topic.scala
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

import scala.collection.immutable.ListSet
import scala.ref.SoftReference

/**
 * Base type for all topics in the website.
 */
trait Topic {

  /** The name of this topic. */
  def name: Name

  /** The description of this topic. */
  def description: Content.Inline

  /** The lead of this topic if one is defined. */
  def lead: Option[Content.Inline] = None

  /** Any settings defined on this topic. */
  def settings: Map[String, String] = Map.empty

  /** The markup content of this topic. */
  def content: Vector[Content.Section] = Vector.empty

  /** The links referenced this topic. */
  def links: Vector[Content.Link] = Vector.empty

  /** The gallery of images associated with this topic. */
  def gallery: Gallery = Gallery.empty

  /** The lazy-loaded collection of subtopics indexed by their ID. */
  final lazy val subtopics: Map[Id, Topic] = {
    val ids = ListSet((staticSubtopics.keys ++ dynamicSubtopicIds()).toSeq: _*)
    if (ids.isEmpty) Map.empty else new Map[Id, Topic] {

      /* The memory-sensitive cache of subtopics. */
      private val cache = collection.mutable.Map[Id, SoftReference[Topic]]()

      /* Try to load the subtopic from the cache. */
      override def get(key: Id): Option[Topic] =
        if (!ids(key)) None else staticSubtopics get key orElse cache.synchronized {
          cache get key match {
            case Some(SoftReference(subtopic)) =>
              Some(subtopic)
            case _ => dynamicSubtopicValue(key) map { subtopic =>
              cache += key -> SoftReference(subtopic)
              subtopic
            }
          }
        }

      /* Try to load subtopics from the cache iteratively. */
      override def iterator: Iterator[(Id, Topic)] =
        ids.iterator flatMap (key => get(key) map (key -> _))

      /* Try to load all subtopics from the cache and add one. */
      override def +[V >: Topic](kv: (Id, V)): Map[Id, V] =
        iterator.toMap + kv

      /* Try to load all subtopics from the cache and subtract one. */
      override def -(key: Id): Map[Id, Topic] =
        iterator.toMap - key
    }
  }

  /** The static subtopics of this topic. */
  protected def staticSubtopics: Map[Id, Topic] = Map.empty

  /** A function that produces the IDs of the dynamic subtopics of this topic. */
  protected def dynamicSubtopicIds: () => Vector[Id] = () => Vector.empty

  /** A function that attempts to produce a dynamic subtopics of this topic with the specified ID. */
  protected def dynamicSubtopicValue: Id => Option[Topic] = _ => None

}

/**
 * Factory for topics.
 */
object Topic {

  /**
   * Creates a new topic.
   *
   * @param name                 The name of the topic.
   * @param description          The description of the topic.
   * @param lead                 The lead of the topic if one is defined.
   * @param settings             Any settings defined on the topic.
   * @param content              The markup content of the topic.
   * @param links                The links referenced the topic.
   * @param gallery              The gallery of images associated with the topic.
   * @param staticSubtopics      The static subtopics indexed by their ID.
   * @param dynamicSubtopicIds   A function that produces the IDs of the dynamic subtopics.
   * @param dynamicSubtopicValue A function that attempts to produce a dynamic subtopic with the specified ID.
   * @return A new topic.
   */
  def apply(
    name: Name,
    description: Content.Inline,
    lead: Option[Content.Inline] = None,
    settings: Map[String, String] = Map.empty,
    content: Vector[Content.Section] = Vector.empty,
    links: Vector[Content.Link] = Vector.empty,
    gallery: Gallery = Gallery.empty
  )(
    staticSubtopics: Map[Id, Topic] = Map.empty,
    dynamicSubtopicIds: () => Vector[Id] = () => Vector.empty,
    dynamicSubtopicValue: Id => Option[Topic] = _ => None
  ): Topic = {
    val _name = name
    val _description = description
    val _lead = lead
    val _settings = settings
    val _content = content
    val _links = links
    val _gallery = gallery
    val _staticSubtopics = staticSubtopics
    val _dynamicSubtopicIds = dynamicSubtopicIds
    val _dynamicSubtopicValue = dynamicSubtopicValue
    new Topic {

      /* Return the name. */
      override def name: Name = _name

      /* Return the description. */
      override def description: Content.Inline = _description

      /* Return the lead. */
      override def lead: Option[Content.Inline] = _lead

      /* Return the settings. */
      override def settings: Map[String, String] = _settings

      /* Return the content. */
      override def content: Vector[Content.Section] = _content

      /* Return the links. */
      override def links: Vector[Content.Link] = _links

      /* Return the gallery. */
      override def gallery: Gallery = _gallery

      /* Return the static subtopics. */
      override protected def staticSubtopics: Map[Id, Topic] = _staticSubtopics

      /* Return the dynamic subtopic IDs. */
      override protected def dynamicSubtopicIds: () => Vector[Id] = _dynamicSubtopicIds

      /* Search the dynamic subtopic providers. */
      override protected def dynamicSubtopicValue: Id => Option[Topic] = _dynamicSubtopicValue

    }
  }

}
