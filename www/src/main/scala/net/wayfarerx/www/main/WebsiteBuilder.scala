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
package main

import java.nio.file.Paths

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{Executors, ThreadFactory}

import concurrent.ExecutionContext

import cats.effect.IO

/**
 * Main entry point for the website builder.
 */
object WebsiteBuilder
  extends FileOperations
    with AssetDeployment
    with PageGenerator
    with MainRuntime
    with App {

  /** The main CPU-bound thread pool. */
  override protected val context: ExecutionContext = ExecutionContext.global

  /** The IO-bound thread pool. */
  override protected val ioContext: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool(new ThreadFactory {
      val group = new ThreadGroup("IO")
      val counter = new AtomicLong

      override def newThread(r: Runnable): Thread =
        new Thread(group, r, s"${group.getName}-${counter.incrementAndGet()}")
    }))

  // Verify the input.
  if (args.length > 0) {
    try println("Usage: website (-h | --help)?") finally System.exit(args.length)
  } else {
    // Construct the program.
    val program = for {
      assets <- Directory.require(Paths.get("www", "src", "main", "assets"))
      destination <- Directory.assume(Paths.get("target", "website"))
      _ <- deployAssets(assets, destination)
      _ <- generatePages(destination)
    } yield ()
    // Execute the program.
    program.runAsync {
      case Left(thrown) => IO(try thrown.printStackTrace() finally System.exit(-1))
      case Right(_) => IO(System.exit(0))
    }.unsafeRunSync()
  }

}
