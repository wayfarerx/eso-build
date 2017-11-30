package net.wayfarerx.www
package main

import cats.effect.IO

import fs2.Stream

object StructureRenderer extends Renderer[Structure] {

  private val CollectionPrefix = " - "
  private val OtherPrefix = "   "
  private val Separator = ": "

  override def render(structure: Structure): Stream[IO, String] =
    renderStructure(structure)

  private def renderValue(value: Value): Stream[IO, String] =
    Stream(value.value)

  private def renderCollection(collection: Collection, depth: Int = 0): Stream[IO, String] = {
    val newLine = Stream(NewLine).covary[IO]
    val prefix = Stream(OtherPrefix * (depth - 1), CollectionPrefix).covary[IO]
    Stream.emits(collection.collection).covary[IO] flatMap { item =>
      newLine ++ prefix ++ (item match {
        case value@Value(_) => renderValue(value)
        case structure@Structure(_) => renderStructure(structure, depth + 1)
      })
    }
  }

  private def renderStructure(structure: Structure, depth: Int = 0): Stream[IO, String] = {
    val newLine = Stream(NewLine).covary[IO]
    val prefix = Stream(OtherPrefix * depth).covary[IO]
    val subPrefix = Stream(OtherPrefix * (depth + 1)).covary[IO]
    Stream.emits(structure.structure.toSeq).covary[IO] map {
      case (key, value) => Stream(key, Separator).covary[IO] ++ (value match {
        case value@Value(_) => renderValue(value)
        case collection@Collection(_) => renderCollection(collection, depth + 1)
        case structure@Structure(_) => newLine ++ subPrefix ++ renderStructure(structure, depth + 1)
      })
    } intersperse newLine ++ prefix flatMap identity
  }

}
