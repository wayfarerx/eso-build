/*
 * Static.scala
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

import java.io._
import java.util.jar.JarFile

import collection.JavaConverters._
import io.Source

/**
 * Deploys the static content.
 */
object Static {

  val Path: String = "net/wayfarerx/www/static/"

  val BufferSize: Int = 1024 * 5

  def main(args: Array[String]): Unit =
    deploy(if (args.length == 0) new File(".") else new File(args(0)))

  def deploy(target: File): Unit = {
    val classpath = System.getProperty("java.class.path", ".") split System.getProperty("path.separator")
    val contributions = classpath map (new File(_)) collect {
      case file if file.isDirectory => Tree(file)
      case file if file.isFile => Archive(file)
    }
    for (contribution <- contributions if contribution.isDirectory("wayfarerx.net/")) {
      println(contribution)
    }
  }

  private def deploy(from: InputStream, to: File, buffer: Array[Byte] = new Array[Byte](BufferSize)): Boolean =
    if (!to.getParentFile.isDirectory && !to.getParentFile.mkdirs()) false else {
      val output = new FileOutputStream(to)
      try {
        var read = from.read(buffer)
        while (read >= 0) {
          if (read > 0) output.write(buffer, 0, read)
          read = from.read(buffer)
        }
        true
      } finally try output.flush() finally output.close()
    }

  /**
   * Base class for items that contribute to the class path.
   */
  sealed trait Contribution {

    def isDirectory(path: String): Boolean

    def isFile(path: String): Boolean

    def list(directoryPath: String): Vector[String]

    def export(filePath: String, destination: File): Boolean

    def dispose(): Unit

  }

  /**
   * Representation of file trees that contribute to the class path.
   *
   * @param location The location of the contributing file tree.
   */
  final class Tree private(location: File) extends Contribution {

    /* Return true for correctly identified directories. */
    override def isDirectory(path: String): Boolean =
      path.endsWith("/") && new File(location, path).isDirectory

    /* Return true for correctly identified files. */
    override def isFile(path: String): Boolean =
      !path.endsWith("/") && new File(location, path).isFile

    /* List the contents of correctly identified directories. */
    override def list(directoryPath: String): Vector[String] =
      if (!isDirectory(directoryPath)) Vector.empty
      else new File(location, directoryPath).listFiles.toVector map { child =>
        if (child.isFile) directoryPath + child.getName else directoryPath + child.getName + "/"
      }

    /* Export the contents of correctly identified files. */
    override def export(filePath: String, destination: File): Boolean =
      if (!isFile(filePath)) false else {
        val input: InputStream = new FileInputStream(new File(location, filePath))
        try deploy(input, destination) finally input.close()
      }

    /* Does nothing. */
    override def dispose(): Unit = ()

  }

  /**
   * Factory for file trees that contribute to the class path.
   */
  object Tree {

    def apply(location: File): Tree =
      if (location.isDirectory) new Tree(location)
      else throw new IllegalArgumentException(s"${location.getAbsolutePath} is not a directory.")

    def unapply(tree: Tree): Boolean = true

  }

  /**
   * Representation of archive files that contribute to the class path.
   *
   * @param location The location of the contributing archive file.
   */
  final class Archive private(location: File) extends Contribution {

    private val jar = new JarFile(location, false)

    private val (directories, files) = {
      val (d, f) = jar.entries().asScala.partition(_.isDirectory)
      (d map (_.getName), f map (_.getName))
    }

    override def isDirectory(path: String): Boolean =
      path.endsWith("/") && Option(jar.getJarEntry(path)).exists(_.isDirectory)

    override def isFile(path: String): Boolean =
      !path.endsWith("/") && Option(jar.getJarEntry(path)).exists(!_.isDirectory)

    override def list(directoryPath: String): Vector[String] = ??? /*
      if (!isDirectory(directoryPath)) Vector.empty
      else jar.getJarEntry((directoryPath)).listFiles.toVector map { child =>
        if (child.isFile) directoryPath + child.getName else directoryPath + child.getName + "/"
      }*/

    override def export(filePath: String, destination: File): Boolean = ???

    override def dispose(): Unit = jar.close()

  }


  /**
   * Factory for archive files that contribute to the class path.
   */
  object Archive {

    def apply(location: File): Archive =
      if (location.isFile) new Archive(location)
      else throw new IllegalArgumentException(s"${location.getAbsolutePath} is not a file.")

    def unapply(archive: Archive): Boolean = true

  }

}
