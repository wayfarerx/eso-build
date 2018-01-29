/*
 * AssetDeployment.scala
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

import cats.effect.IO
import fs2.{Stream, io}

import scala.concurrent.ExecutionContext

/**
 * Strategy for deploying the static file assets in the website.
 */
trait AssetDeployment {
  self: Context =>

  /** The size of the buffer to use. */
  def assetBufferSize: Int = 1024 * 4

  /** The maximum number of IO threads to use. */
  def assetParallelism: Int = 8

  /**
   * Deploys a collection of source assets to a destination.
   *
   * @return The effect of the asset deployment.
   */
  final def deployAssets: IO[Unit] = for {
    _ <- IO.shift(ioExecutionContext)
    streams <- IO.pure(findAllAssets(assetDirectory, targetDirectory) map { case (d, f, t) => deployAsset(d, f, t) })
    _ <- IO.shift
    result <- {
      implicit val executionContext: ExecutionContext = ioExecutionContext
      streams.join(assetParallelism).fold(())((_, _) => ()).run
    }
  } yield result

  /**
   * Finds all the assets from the source directory and returns the profile for their deployment.
   *
   * @param source      The directory to search for assets.
   * @param destination The directory to deploy any found assets to.
   * @return The effectual stream of asset discovery.
   */
  private def findAllAssets(source: Directory, destination: Directory): Stream[IO, (Option[Directory], File, File)] =
    source.search flatMap {
      case from@File(_) => Stream eval (
        for {
          fromModified <- from.lastModified
          to <- from.rebase(source, destination)
          toExists <- to.exists
          toModified <- to.lastModified
          toParent <- to.parent
        } yield Asset(from, fromModified, to, toExists, toModified, toParent)
        ) filter { a => a.fromModified >= a.toModified
      } map (a => (a.toParent, a.from, a.to))
      case _ =>
        Stream.empty
    }

  /**
   * Deploys the specified asset.
   *
   * @param directory The directory that contains the file to deploy to.
   * @param from      The location to deploy from.
   * @param to        The location to deploy to.
   * @return A stream that deploys the specified asset.
   */
  private def deployAsset(directory: Option[Directory], from: File, to: File): Stream[IO, Unit] = for {
    _ <- Stream eval (directory map (_.create) getOrElse IO.pure(()))
    result <- io.file.readAll[IO](from.path, assetBufferSize) through io.file.writeAll(to.path)
  } yield result

  /**
   * Internal class for representing data collected about an asset that may be deployed.
   *
   * @param from         The asset that may be deployed.
   * @param fromModified The last time the asset that may be deployed was modified.
   * @param to           The location that the asset would be deployed to.
   * @param toExists     True if data exists at the location the asset would be deployed to.
   * @param toModified   The last time data at the location that would be deployed to was modified.
   * @param toParent     The parent directory of the location would be deployed to.
   */
  private case class Asset(
    from: File,
    fromModified: Long,
    to: File,
    toExists: Boolean,
    toModified: Long,
    toParent: Option[Directory])

}