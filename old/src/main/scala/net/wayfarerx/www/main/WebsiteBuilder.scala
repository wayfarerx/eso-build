/*
 * WebsiteBuilder.scala
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
package home

import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{Executors, ThreadFactory}

import concurrent.ExecutionContext

import cats.effect.IO

import drinks.Drinks

/**
 * Main entry point for the website builder.
 */
object WebsiteBuilder extends App {

  // Verify the input.
  if (args.length > 0) {
    try println("Usage: website (-h | --help)?") finally System.exit(args.length)
  } else {
    // Construct the program.
    val program = for {
      root <- Directory.require(Paths.get("."))
      asset <- Directory.require(Paths.get("www", "src", "main", "assets"))
      target <- Directory.assume(Paths.get("target", "website"))
      task = new Task(root, asset, target)
      _ <- task.deployAssets
      _ <- task.generatePages
    } yield ()
    // Execute the program.
    program.runAsync {
      case Left(thrown) => IO(try thrown.printStackTrace() finally System.exit(-1))
      case Right(_) => IO(System.exit(0))
    }.unsafeRunSync()
  }

  /**
   * The task that implements the website construction.
   *
   * @param rootDirectory   The root directory of the project.
   * @param assetDirectory  The directory to copy assets from.
   * @param targetDirectory The directory to construct the website in.
   */
  private final class Task(
    override val rootDirectory: Directory,
    override val assetDirectory: Directory,
    override val targetDirectory: Directory)
    extends StructureRenderer
      with ContentRenderer
      with EntityRenderer
      with AssetDeployment
      with PageGenerator
      with Context {

    /* The main CPU-bound thread pool. */
    override val executionContext: ExecutionContext = ExecutionContext.global

    /* The IO-bound thread pool. */
    override val ioExecutionContext: ExecutionContext =
      ExecutionContext.fromExecutor(Executors.newCachedThreadPool(new ThreadFactory {
        val group = new ThreadGroup("IO")
        val counter = new AtomicLong

        override def newThread(r: Runnable): Thread =
          new Thread(group, r, s"${group.getName}-${counter.incrementAndGet()}")
      }))

    /** The root home page in the website. */
    override val homePage: Home = new Home {

      override def name: String = "home"

      override def displayName: String = "wayfarerx.net"

      override def title: String = ""

      override def description: String = "The misadventures of wayfarerx."

      override def children: Vector[Component] = Vector(
        Drinks,
        new Topic {
          override def displayName: String = "games"
          override def title: String = "Games"
          override def description: String = "Gaming is fun."
        },
        new Topic {
          override def displayName: String = "code"
          override def title: String = "Code"
          override def description: String = "Hacking is fun."
        },
        new Topic {
          override def displayName: String = "thoughts"
          override def title: String = "Thoughts"
          override def description: String = "Thinking is fun."
        })

    }

  }

}
