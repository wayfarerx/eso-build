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

  /**
   * Base for type classes that can render information.
   *
   * @tparam T The type of object to render.
   */
  trait Renderer[-T] {

    /**
     * Renders an object to a stream of strings.
     *
     * @param renderable The object to render.
     */
    def render(renderable: T): Stream[IO, String]

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
   * Describes the concurrent runtime environment of the application.
   */
  trait Context {

    /** The main CPU-bound thread pool. */
    implicit def executionContext: ExecutionContext

    /** The IO-bound thread pool. */
    def ioExecutionContext: ExecutionContext

    /** The root directory to use. */
    def rootDirectory: Directory

    /** The asset directory to use. */
    def assetDirectory: Directory

    /** The target directory to use. */
    def targetDirectory: Directory

    /** The home page of the site. */
    def homePage: Home

  }

}
