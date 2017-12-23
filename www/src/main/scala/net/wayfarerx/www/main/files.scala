/*
 * FileOperations.scala
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

import collection.JavaConverters._

import java.nio.file.{Files, Path}

import cats.effect.IO

import fs2.Stream

/**
 * Base class for all filesystem resources.
 */
sealed trait Resource {

  /** The type of this resource. */
  type This <: Resource

  /** The path to this resource. */
  def path: Path

  /** Attempts to determine if this resource exists in the filesystem. */
  def exists: IO[Boolean]

  /** Attempts to return the parent of this resource if one exists. */
  final def parent: IO[Option[Directory]] =
    Option(path.getParent) match {
      case Some(parentPath) => Directory.assume(parentPath) map (Some(_))
      case None => IO.pure(None)
    }

  /** Attempts to determine the last time this resource was modified in the filesystem. */
  final def lastModified: IO[Long] = for {
    e <- exists
    m <- if (e) IO(Files.getLastModifiedTime(path).toInstant.toEpochMilli) else IO.pure(0L)
  } yield m

  /**
   * Attempts to change out one filesystem ancestor for another.
   *
   * @param from The filesystem ancestor to abandon.
   * @param to   The filesystem ancestor to adopt.
   * @return A new resource representing the rebasing of this resource.
   */
  final def rebase(from: Directory, to: Directory): IO[This] =
    if (!path.startsWith(from.path)) IO.raiseError(new IllegalArgumentException("Invalid ancestor: " + path.toString))
    else create(to.path.resolve(path.subpath(from.path.getNameCount, path.getNameCount)))

  /**
   * Creates a new instance of this type of resource.
   *
   * @param path The path of the new instance.
   * @return A new instance of this type of resource.
   */
  protected def create(path: Path): IO[This]

}

/**
 * Factory for resources.
 */
object Resource {

  /**
   * Creates a representation of a resource if it already exists in the filesystem.
   *
   * @param path The path to the resource.
   * @return A representation of a resource if it already exists in the filesystem.
   */
  def attempt(path: Path): IO[Option[Resource]] = IO {
    if (Files.isDirectory(path)) Some(Directory(path))
    else if (Files.isRegularFile(path)) Some(File(path))
    else None
  }

  /**
   * Creates a representation of a resource that must already exist in the filesystem.
   *
   * @param path The path to the resource.
   * @return A representation of a resource in the filesystem.
   */
  def require(path: Path): IO[Resource] = for {
    resourceOpt <- attempt(path)
    resource <- resourceOpt map IO.pure getOrElse
      IO.raiseError(new IllegalArgumentException("Require path: " + path.toString))
  } yield resource

}

/**
 * Representation of regular files and links to regular files.
 *
 * @param path The path to this file.
 */
case class File private (path: Path) extends Resource {

  /* Set the type of this resource. */
  override type This = File

  /* Determine if this file exists in the filesystem. */
  override def exists: IO[Boolean] =
    IO(Files.isRegularFile(path))

  /* Create a file resource. */
  override protected def create(path: Path): IO[File] =
    File.assume(path)

}

/**
 * Factory for files.
 */
object File {

  /**
   * Creates a representation of a file that may or may not exist in the filesystem.
   *
   * @param path The path to the file.
   * @return A representation of a file that may or may not exist in the filesystem.
   */
  def assume(path: Path): IO[File] = for {
    fileOpt <- IO(if (!Files.exists(path) || Files.isRegularFile(path)) Some(File(path)) else None)
    file <- fileOpt map IO.pure getOrElse
      IO.raiseError(new IllegalStateException("Assume file: " + path.toString))
  } yield file

  /**
   * Creates a representation of a file if it already exists in the filesystem.
   *
   * @param path The path to the file.
   * @return A representation of a file if it already exists in the filesystem.
   */
  def attempt(path: Path): IO[Option[File]] = IO {
    if (Files.isRegularFile(path)) Some(File(path))
    else None
  }

  /**
   * Creates a representation of a file that must already exist in the filesystem.
   *
   * @param path The path to the file.
   * @return A representation of a file in the filesystem.
   */
  def require(path: Path): IO[File] = for {
    fileOpt <- attempt(path)
    file <- fileOpt map IO.pure getOrElse
      IO.raiseError(new IllegalArgumentException("Require file: " + path.toString))
  } yield file

}

/**
 * Representation of directories and links to directories.
 *
 * @param path The path to this directory.
 */
case class Directory private (path: Path) extends Resource {

  /* Set the type of this resource. */
  override type This = Directory

  /* Determine if this directory exists in the filesystem. */
  override def exists: IO[Boolean] =
    IO(Files.isDirectory(path))

  /**
   * Ensures that the file system has an appropriate entry for this directory.
   *
   * @return The resulting directory.
   */
  def create: IO[Directory] =
    for (path <- IO(Files.createDirectories(path))) yield new Directory(path)

  /**
   * Returns all the immediate children of this directory as a stream.
   *
   * @return The immediate children of this directory as a stream.
   */
  def list: Stream[IO, Resource] = for {
    streamed <- Stream.force(IO(Stream.emits(Files.list(path).iterator().asScala.toSeq).covary[IO]))
    resolved <- Stream.eval(Resource.require(streamed))
  } yield resolved

  /**
   * Returns all the descendants of this directory as a stream.
   *
   * @return The descendants of this directory as a stream.
   */
  def search: Stream[IO, Resource] = for {
    child <- list
    descendant <- child match {
      case file@File(_) => Stream.emit(file).covary[IO]
      case directory@Directory(_) => Stream.emit(directory).covary[IO] ++ directory.search
    }
  } yield descendant

  /* Create a file resource. */
  override protected def create(path: Path): IO[Directory] =
    Directory.assume(path)

}

/**
 * Factory for directories.
 */
object Directory {

  /**
   * Creates a representation of a directory that may or may not exist in the filesystem.
   *
   * @param path The path to the directory.
   * @return A representation of a directory that may or may not exist in the filesystem.
   */
  def assume(path: Path): IO[Directory] = for {
    directoryOpt <- IO(if (!Files.exists(path) || Files.isDirectory(path)) Some(Directory(path)) else None)
    directory <- directoryOpt map IO.pure getOrElse
      IO.raiseError(new IllegalStateException("Assume directory: " + path.toString))
  } yield directory

  /**
   * Creates a representation of a directory if it already exists in the filesystem.
   *
   * @param path The path to the directory.
   * @return A representation of a directory if it already exists in the filesystem.
   */
  def attempt(path: Path): IO[Option[Directory]] = IO {
    if (Files.isDirectory(path)) Some(Directory(path))
    else None
  }

  /**
   * Creates a representation of a directory that must already exist in the filesystem.
   *
   * @param path The path to the directory.
   * @return A representation of a directory in the filesystem.
   */
  def require(path: Path): IO[Directory] = for {
    directoryOpt <- attempt(path)
    directory <- directoryOpt map IO.pure getOrElse
      IO.raiseError(new IllegalArgumentException("Require directory: " + path.toString))
  } yield directory

}
