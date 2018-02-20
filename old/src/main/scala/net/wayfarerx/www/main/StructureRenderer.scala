package net.wayfarerx.www
package home

import cats.effect.IO

import fs2.Stream

trait StructureRenderer {
  self: ContentRenderer =>

  private val CollectionPrefix = " - "
  private val OtherPrefix = "   "
  private val Separator = ": "

  /** The structure rendering strategy. */
  implicit val structureRenderer: Renderer[Structure] = new Renderer[Structure] {

    override def render(structure: Structure): Stream[IO, String] =
      renderStructure(structure)

  }

  private def renderValue(value: Value): Stream[IO, String] =
    conditionData(Stream(value.value))

  private def renderRendered(rendered: Rendered): Stream[IO, String] =
    conditionData(rendered.content.render)

  private def renderCollection(collection: Collection, depth: Int = 0): Stream[IO, String] = {
    val newLine = Stream(NewLine).covary[IO]
    val prefix = Stream(OtherPrefix * (depth - 1), CollectionPrefix).covary[IO]
    Stream.emits(collection.collection).covary[IO] flatMap { item =>
      newLine ++ prefix ++ (item match {
        case value@Value(_) => renderValue(value)
        case rendered@Rendered(_) => renderRendered(rendered)
        case structure@Structure(_) => renderStructure(structure, depth + 1)
      })
    }
  }

  private def renderStructure(structure: Structure, depth: Int = 0): Stream[IO, String] = {
    val newLine = Stream(NewLine).covary[IO]
    val prefix = Stream(OtherPrefix * (depth - 1)).covary[IO]
    val subPrefix = Stream(OtherPrefix * depth).covary[IO]
    Stream.emits(structure.structure.toSeq).covary[IO] map {
      case (key, value) => Stream(key, Separator).covary[IO] ++ (value match {
        case value@Value(_) => renderValue(value)
        case rendered@Rendered(_) => renderRendered(rendered)
        case collection@Collection(_) => renderCollection(collection, depth + 1)
        case structure@Structure(_) => newLine ++ subPrefix ++ renderStructure(structure, depth + 1)
      })
    } intersperse newLine ++ prefix flatMap identity
  }

  private def conditionData(data: Stream[IO, String]): Stream[IO, String] =
    Stream("'") ++ data.map(_.replace("'", "''").replaceAll("(\\s)+", " ")).zipWithPreviousAndNext.map {
      case (previous, current, next) =>
        val prefixed = previous match {
          case Some(p) if p.matches(".*(\\s+)$") => current.replaceAll("^\\s+", "")
          case Some(_) => current
          case None => current.replaceAll("^\\s+", "")
        }
        if (next.isEmpty) prefixed.replaceAll("\\s+$", "") else prefixed
    }.filter(_.nonEmpty) ++ Stream("'")

}