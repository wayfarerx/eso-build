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

  /** The lazy-loaded collection of subtopics indexed by their ID. */
  final lazy val subtopics: Map[Id, Topic] = {
    val ids = ListSet((staticSubtopics.keys ++ dynamicSubtopicIds).toSeq: _*)
    if (ids.isEmpty) Map.empty else new Map[Id, Topic] {

      /* The memory-sensitive cache of subtopics. */
      private val cache = collection.mutable.Map[Id, SoftReference[Topic]]()

      /* Try to load the subtopic from the cache. */
      override def get(key: Id): Option[Topic] =
        if (!ids(key)) None else cache synchronized {
          cache get key match {
            case Some(SoftReference(subtopic)) =>
              Some(subtopic)
            case _ => loadDynamicSubtopic(key) map { subtopic =>
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

  /** The name of this topic. */
  def name: Name

  /** The description of this topic. */
  def description: Content.Inline

  /** The parent of this this topic. */
  def parent: Option[Topic] = None

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

  /** The static subtopics of this topic. */
  protected def staticSubtopics: Map[Id, Topic] = Map.empty

  /** The IDs of the dynamic subtopics of this topic. */
  protected def dynamicSubtopicIds: Vector[Id] = Vector.empty

  /**
   * Loads the dynamic subtopic with the specified ID if it exists.
   *
   * @param id The ID of the subtopic to load.
   * @return The dynamic subtopic with the specified ID if it exists.
   */
  protected def loadDynamicSubtopic(id: Id): Option[Topic] = None

}

/**
 * Factory for topics.
 */
object Topic {

  /**
   * Creates a new topic.
   *
   * @param name The name of the topic.
   * @param description The description of the topic.
   * @param parent The parent of the the topic.
   * @param lead The lead of the topic if one is defined.
   * @param settings Any settings defined on the topic.
   * @param content The markup content of the topic.
   * @param links The links referenced the topic.
   * @param gallery The gallery of images associated with the topic.
   * @param subtopics The collection of subtopics indexed by their ID.
   * @param subtopicIds The supplemental subtopic ID provider.
   * @param subtopicLoader The supplemental subtopic loader.
   * @return A new topic.
   */
  def apply(
    name: Name,
    description: Content.Inline,
    parent: Option[Topic] = None,
    lead: Option[Content.Inline] = None,
    settings: Map[String, String] = Map.empty,
    content: Vector[Content.Section] = Vector.empty,
    links: Vector[Content.Link] = Vector.empty,
    gallery: Gallery = Gallery.empty,
    subtopics: Map[Id, Topic] = Map.empty,
    subtopicIds: => Vector[Id] = Vector.empty,
    subtopicLoader: Id => Option[Topic] = _ => None
  ): Topic = {
    val _name = name
    val _description = description
    val _parent = parent
    val _lead = lead
    val _settings = settings
    val _content = content
    val _links = links
    val _gallery = gallery
    val _subtopics = subtopics
    new Topic {

      /* Return the name. */
      override def name: Name = _name

      /* Return the description. */
      override def description: Content.Inline = _description

      /** The parent of this this topic. */
      override def parent: Option[Topic] = _parent

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

      /* Return the primary and supplemental subtopic IDs. */
      override protected def dynamicSubtopicIds: Vector[Id] =
        _subtopics.keys ++: subtopicIds

      /* Search the primary and supplemental subtopic providers. */
      override protected def loadDynamicSubtopic(id: Id): Option[Topic] =
        _subtopics get id orElse subtopicLoader(id)

    }
  }

  /**
   * A disambiguated visitor for all topics.
   */
  trait Visitor {

    // def apply()

  }

}
