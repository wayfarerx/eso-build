/*
 * Context.scala
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

/**
 * The context that components operate in.
 */
trait Context {

  /**
   * The current working directory as an absolute path in the website.
   *
   * @return The current working directory as an absolute path in the website.
   */
  def working: Location.Absolute

}

/**
 * Factory for contexts.
 */
object Context {

  /** The default context. */
  implicit val Default: Context = new Context {

    /* Always use the root context. */
    override val working: Location.Absolute = /()

  }

  /**
   * Creates an implementation of a context.
   *
   * @param working The current working directory as an absolute path in the website.
   */
  def apply(working: Location.Absolute): Context = Support(working)

  /**
   * Default implementation of a context.
   *
   * @param working The current working directory as an absolute path in the website.
   */
  case class Support(working: Location.Absolute) extends Context

}
