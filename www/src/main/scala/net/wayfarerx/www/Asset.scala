/*
 * Asset.scala
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

package net.wayfarerx.www

import org.apache.commons.io.IOUtils

/**
 * Data of any type in a classpath resource.
 *
 * @param path The path of this asset.
 */
final class Asset(val path: String) extends AnyVal {

  /** The name of this asset without an extension. */
  def name: String = {
    val start = path.lastIndexOf('/') + 1
    path.substring(start, path.lastIndexOf('.', start) match {
      case lastDot if lastDot >= start => lastDot
      case _ => path.length
    })
  }

  /**
   * True if this asset exists.
   *
   * @param loader The asset loader to use.
   */
  def exists(implicit loader: Asset.Loader): Boolean =
    loader.exists(path)

  /**
   * True if this asset is a directory.
   *
   * @param loader The asset loader to use.
   */
  def isDirectory(implicit loader: Asset.Loader): Boolean =
    loader.isDirectory(path)

  /**
   * True if this asset is a file.
   *
   * @param loader The asset loader to use.
   */
  def isFile(implicit loader: Asset.Loader): Boolean =
    loader.isFile(path)

  /**
   * Attempts to determine the MIME type of this asset.
   *
   * @param loader The asset loader to use.
   */
  def mimeType(implicit loader: Asset.Loader): Option[String] =
    if (!isFile) None else path substring path.lastIndexOf('.') + 1 match {
      case "md" | "markdown" => Some(Asset.MIME.Markdown)
      case "properties" => Some(Asset.MIME.Properties)
      case "gif" => Some(Asset.MIME.GIF)
      case "jpg" | "jpeg" => Some(Asset.MIME.JPEG)
      case "png" => Some(Asset.MIME.PNG)
      case _ => None
    }

  /**
   * Attempts to locate a child asset.
   *
   * @param name The name of the child asset.
   * @return The requested child asset.
   */
  def apply(name: String)(implicit loader: Asset.Loader): Option[Asset] =
    Asset(this, name)

  /**
   * Lists the child paths of this directory asset.
   *
   * @param loader The asset loader to use.
   * @return The child paths of this directory asset.
   */
  def list()(implicit loader: Asset.Loader): Vector[Asset] =
    loader list path map (Asset(_))

  /**
   * Loads the binary content of this file asset.
   *
   * @param loader The asset loader to use.
   * @return The binary content of this file asset.
   */
  def load()(implicit loader: Asset.Loader): Array[Byte] =
    loader load path

  /**
   * Loads the textual content of this file asset.
   *
   * @param loader The asset loader to use.
   * @return The textual content of this file asset.
   */
  def loadText()(implicit loader: Asset.Loader): String =
    new String(load(), "UTF-8")

  /* Return the path. */
  override def toString: String = path

}

/**
 * Defines the asset loader.
 */
object Asset {

  /**
   * Creates a new asset.
   *
   * @param path The path of the asset.
   * @return A new asset.
   */
  def apply(path: String): Asset =
    new Asset(path)

  /**
   * Creates a new asset.
   *
   * @param parent The parent asset.
   * @param name   The name of the child asset.
   * @return A new asset.
   */
  def apply(parent: Asset, name: String)(implicit loader: Asset.Loader): Option[Asset] =
    loader.resolve(parent.path, name) map apply

  /**
   * A class path bound loader of asset data and metadata.
   *
   * @param classLoader The class loader to use.
   */
  final class Loader(val classLoader: ClassLoader) extends AnyVal {

    /**
     * Returns true if the specified asset exists.
     *
     * @param path The path to the asset.
     * @return True if the specified asset exists.
     */
    def exists(path: String): Boolean =
      !path.endsWith(".class") && classLoader.getResource(path) != null

    /**
     * Returns true if the specified asset is a directory.
     *
     * @param path The path to the asset.
     * @return True if the specified asset is a directory.
     */
    def isDirectory(path: String): Boolean =
      exists(path) && !path.contains(".")

    /**
     * Returns true if the specified asset is a file.
     *
     * @param path The path to the asset.
     * @return True if the specified asset is a file.
     */
    def isFile(path: String): Boolean =
      exists(path) && path.contains(".")

    /**
     * Lists the paths contained by the specified asset.
     *
     * @param path The path to the asset.
     * @return The paths contained by the specified asset.
     */
    def list(path: String): Vector[String] =
      if (!isDirectory(path)) Vector() else {
        val prefix = if (path endsWith "/") path else path + "/"
        val source = scala.io.Source.fromResource(path, classLoader)
        try source.getLines.filterNot(_ endsWith ".class").map(prefix + _).toVector
        finally source.close()
      }

    /**
     * Resolves an asset with the specified parent and name.
     *
     * @param parent The parent of the desired asset.
     * @param name   The name of the desired asset.
     * @return The asset with the specified parent and name.
     */
    def resolve(parent: String, name: String): Option[String] =
      if (!isDirectory(parent) || name.endsWith(".class")) None else {
        val prefix = if (parent endsWith "/") parent else parent + "/"
        val child = prefix + name
        Some(child) filter exists
      }

    /**
     * Loads the content of the specified asset.
     *
     * @param path The path to the asset.
     * @return The content of the specified asset.
     */
    def load(path: String): Array[Byte] =
      if (!isFile(path)) Array() else {
        val connection = classLoader.getResource(path).openConnection()
        connection.connect()
        val length = connection.getContentLength
        IOUtils.readFully(connection.getInputStream, length)
      }

  }

  /**
   * Defines the default asset loader.
   */
  object Loader {

    /** The default asset loader. */
    implicit val Default: Loader = new Loader(Asset.getClass.getClassLoader)

    /**
     * Creates a new asset loader.
     *
     * @param classLoader The class loader to use.
     * @return A new asset loader.
     */
    def apply(classLoader: ClassLoader): Loader =
      new Loader(classLoader)

  }

  /**
   * Common MIME types.
   */
  object MIME {

    /** The MIME type for markdown. */
    val Markdown: String = "text/markdown; charset=UTF-8"

    /** The MIME type for Java properties. */
    val Properties: String = "text/x-java-properties; charset=UTF-8"

    /** The MIME type for GIF images. */
    val GIF: String = "image/gif"

    /** The MIME type for JPEG images. */
    val JPEG: String = "image/jpeg"

    /** The MIME type for PNG images. */
    val PNG: String = "image/png"

  }

}
