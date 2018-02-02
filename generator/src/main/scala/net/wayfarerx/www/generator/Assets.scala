package net.wayfarerx.www.generator

import collection.JavaConverters._

import java.nio.file.{Files, Path}

object Assets {

  @volatile
  private var _root: Option[Path] = None

  def root: Path = _root getOrElse { throw new IllegalStateException }

  def list: Vector[Path] = _root map { assets =>
    val stream = Files.walk(assets)
    try stream.iterator.asScala.filter(Files.isRegularFile(_)).toVector finally stream.close()
  } getOrElse Vector()

  def get(location: String): Option[Path] =
    _root map (_.resolve(if (location.startsWith("/")) location.drop(1) else location)) filter (Files.isRegularFile(_))

  private[generator] def initialize(projectDirectory: Path): Unit = {
    val root = Some(projectDirectory.resolve("www/src/main/assets/"))
    if (_root.isDefined) throw new IllegalStateException else synchronized {
      if (_root.isDefined) throw new IllegalStateException else _root = root
    }
  }

}
