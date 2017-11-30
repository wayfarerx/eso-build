/*
 * PageGenerator.scala
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
package main

import concurrent.ExecutionContext

import cats.effect.IO

import fs2.{Stream, io, text}

/**
 * Strategy for generating pages in the website.
 */
trait PageGenerator extends FileOperations {
  self: MainRuntime =>

  /** The maximum number of IO threads to use. */
  protected def pageParallelism: Int = 8

  /** The root landing page in the website. */
  private val LandingPage = new Landing {

    override def name: String = "landing"

    override def location: String = "/"

    override def title: String = ""

    override def description: Content = "The misadventures of wayfarerx."

    override def imageDescription: Option[String] = None

  }

  /**
   * Generates all the pages in the website into the specified directory.
   *
   * @param destination The directory to generate pages into.
   * @return The effect of generating the pages.
   */
  protected final def generatePages(destination: Directory): IO[Unit] = for {
    _ <- IO.shift(ioContext)
    streams <- IO.pure(findAllEntities(destination, LandingPage) map (generatePage _).tupled)
    _ <- IO.shift
    result <- {
      implicit val context: ExecutionContext = ioContext
      streams.join(pageParallelism).fold(())((_, _) => ()).run
    }
  } yield result

  /**
   * Finds all the entities from the specified entity down and returns their directory mappings.
   *
   * @param destination The destination directory that entities will resolve against.
   * @param entity      The entity to discover all entities under.
   * @return The effectual stream of entity discovery.
   */
  private def findAllEntities(destination: Directory, entity: Entity): Stream[IO, (Directory, Entity)] =
    entity match {
      case landing: Landing =>
        Stream(destination -> landing) ++
          Stream.emits(landing.components).flatMap(findAllEntities(destination, _))
      case index: Index =>
        Stream.eval(Directory.assume(destination.path.resolve(index.id))).map(_ -> index) ++
          Stream.emits(index.components).flatMap(findAllEntities(destination, _))
      case article: Article =>
        Stream.eval(Directory.assume(destination.path.resolve(article.id))).map(_ -> article)
    }

  /**
   * Generates a page for an entity in a specific directory.
   *
   * @param directory The directory to generate a page into.
   * @param entity    The entity to generate a page for.
   * @return A stream that generates the requested page.
   */
  private def generatePage(directory: Directory, entity: Entity): Stream[IO, Unit] = for {
    dir <- Stream eval directory.create
    file <- Stream eval File.assume(dir.path.resolve("index.html"))
    result <- entity.render through text.utf8Encode through io.file.writeAll(file.path)
  } yield result

}
