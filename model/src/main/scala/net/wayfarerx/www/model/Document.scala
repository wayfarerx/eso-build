/*
 * Document.scala
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

import language.implicitConversions

/**
 * A document that describes a single topic.
 *
 * @param name        The name of this document.
 * @param author      The author of this document.
 * @param description The description of this document.
 * @param content     The root content of this document.
 * @param sections    The sections in this document.
 */
case class Document(
  name: Name,
  author: Option[Author],
  description: Markup,
  content: Markup,
  sections: Vector[Section]
) {

  /** The title of this document. */
  def title: String = name.display

  /** The image for this document. */
  def image: Asset.Image.Single = Document.Image

}

/**
 * Definitions associated with documents.
 */
object Document {

  /** The image selector for all documents. */
  private def Image = Asset.Image.Single(Name("image"))

}