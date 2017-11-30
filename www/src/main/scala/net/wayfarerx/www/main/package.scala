/*
 * package.scala
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

import java.io.PrintWriter

import concurrent.ExecutionContext

import cats.effect.IO

import fs2.Stream

/**
 * Global declarations for the main package.
 */
package object main {

  /** The indent to use when rendering text. */
  val Indent: String = "  "

  /** The text that denotes a line break. */
  val NewLine: String = "\r\n"

  /** The content rendering strategy. */
  implicit def ImplicitContentRenderer: Renderer[Content] = ContentRenderer

  /** The structure rendering strategy. */
  implicit def ImplicitStructureRenderer: Renderer[Structure] = StructureRenderer

  /** The entity rendering strategy. */
  implicit def ImplicitEntityRenderer: Renderer[Entity] = EntityRenderer

  /**
   * Base for type classes that can render data, content & entities.
   *
   * @tparam T The type of object to render.
   */
  trait Renderer[-T] {

    /**
     * Renders an object to a stream of strings.
     *
     * @param value The value to render.
     */
    def render(value: T): Stream[IO, String]

  }

  /**
   * Adds a render method to any object with a renderer type class.
   *
   * @param renderable The renderable object.
   * @tparam T The type of the renderable object.
   */
  final implicit class Renderable[T: Renderer](renderable: T) {

    /**
     * Renders the underlying object to a print writer.
     */
    def render: Stream[IO, String] =
      implicitly[Renderer[T]].render(renderable)

  }

  /**
   * Describes the concurrency runtime environment of the application.
   */
  trait MainRuntime {

    /** The main CPU-bound thread pool. */
    protected implicit def context: ExecutionContext

    /** The IO-bound thread pool. */
    protected def ioContext: ExecutionContext

  }

}
