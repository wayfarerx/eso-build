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
package model

import util.{Success, Try}

/**
 * Base type for entities that only contain a document.
 */
trait Topic {

  /** The document that describes this topic. */
  def document: Document

}

/**
 * Definitions associated with topics.
 */
object Topic {

  /**
   * Creates a decoder for a type of topic.
   *
   * @tparam T The type of topic to decode.
   * @param decoder The function that decodes a document.
   * @return A decoder for a type of topic.
   */
  def entity[T <: Topic](decoder: Document => T): Entity[T] = new Entity[T] {

    /* Returns the description of this entity. */
    override def description(context: Context, entity: T): Markup = entity.document.description

    /* Attempts to encode a document from an entity of the underlying type. */
    override def encode(context: Context, entity: T): Try[Document] = Success(entity.document)

    /* Attempts to decode a document into an entity of the underlying type. */
    override def decode(context: Context, document: Document): Try[T] = Try(decoder(document))
  }


}
