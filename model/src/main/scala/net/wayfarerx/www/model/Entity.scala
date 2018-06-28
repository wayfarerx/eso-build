/*
 * Entity.scala
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

import util.Try

/**
 * An entity transformer.
 *
 * @tparam T The type to transform to.
 */
trait Entity[T] {

  /**
   * Returns the description of this entity.
   *
   * @param context The context to describe in.
   * @param entity The entity to describe.
   * @return The description of this entity.
   */
  def description(context: Context, entity: T): Markup

  /**
   * Attempts to decode a document into an entity of the underlying type.
   *
   * @param context The context to decode in.
   * @param document The document to decode.
   * @return The result of attempting to decode the specified document.
   */
  def decode(context: Context, document: Document): Try[T]

  /**
   * Attempts to encode a document from an entity of the underlying type.
   *
   * @param context The context to encode in.
   * @param entity The entity to encode.
   * @return The result of attempting to encode the specified entity.
   */
  def encode(context: Context, entity: T): Try[Document]

}
