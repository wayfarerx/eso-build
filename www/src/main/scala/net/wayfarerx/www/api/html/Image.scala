/*
 * Image.scala
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

package net.wayfarerx.www.api
package html

/**
 * Represents an `<img>` tag.
 *
 * @param src     The location of the image file.
 * @param alt     The alternate text associated with this image.
 * @param id      The ID of this image.
 * @param classes The classes specified for this image.
 */
case class Image(
  src: Location,
  alt: String,
  id: Option[String] = None,
  classes: Vector[String] = Vector())
  extends Tag {

  /* Set image as the tag type. */
  override type This = Image

  /* Copy this image tag using the specified ID and classes. */
  override def copyTag(id: Option[String], classes: Vector[String]): This = copy(id = id, classes = classes)

}
