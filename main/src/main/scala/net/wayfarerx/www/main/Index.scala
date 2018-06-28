/*
 * Index.scala
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
package main

import model._

import util.{Failure, Success, Try}

/**
 * An index of all nodes by their name, title, location and entity type.
 *
 * @param root The root node of the site.
 */
final class Index(root: Node) {

  /** The nodes indexed by their their name, title, location and entity type. */
  private lazy val (byLocation, byAlias): (Map[Location, Node], Map[Name, Map[Class[_], Vector[Node]]]) = {

    /* Scan the entire site recursively. */
    @annotation.tailrec
    def scan(
      input: Vector[Node],
      outputByLocation: Map[Location, Node],
      outputByAlias: Map[Name, Map[Class[_], Vector[Node]]]
    ): Try[(Map[Location, Node], Map[Name, Map[Class[_], Vector[Node]]])] =
      if (input.isEmpty) Success(outputByLocation -> outputByAlias) else {
        val node = input.head
        node.title map (Name(_)) map {
          case title if title != node.name => Vector(node.name, title)
          case _ => Vector(node.name)
        } flatMap (a => node.children map (a -> _)) match {
          case Success((aliases, children)) =>
            scan(
              children ++ input.tail,
              outputByLocation + (node.location -> node),
              (outputByAlias /: aliases) { (output, alias) =>
                val nested = output.getOrElse(alias, Map())
                output + (alias -> (nested + (node.cls -> (nested.getOrElse(node.cls, Vector()) :+ node))))
              }
            )
          case Failure(thrown) =>
            Failure(thrown)
        }
      }

    scan(Vector(root), Map(), Map()).get
  }

  /**
   * Returns the node with the specified location and a compatible type.
   *
   * @param location The location of the node to return.
   * @param cls      The type the node must be assignable to.
   * @return The node with the specified location and compatible type.
   */
  def apply(location: Location, cls: Class[_]): Option[Node] =
    byLocation get location filter (cls isAssignableFrom _.cls)

  /**
   * Returns the closest nodes with the specified alias and a compatible type.
   *
   * @param from  The node the search is coming from.
   * @param alias The alias to search for.
   * @param cls   The class that the result must extend.
   * @return The closest nodes with the specified alias.
   */
  def apply(from: Node, alias: Name, cls: Class[_]): Vector[Node] =
    byAlias get alias map { nested =>
      val matching = nested.filterKeys(cls.isAssignableFrom).values.flatten.map(n => n -> from.distanceTo(n)).toVector
      if (matching.isEmpty) Vector.empty else {
        val closest = matching.minBy(_._2)._2
        matching filter (_._2 == closest) map (_._1)
      }
    } getOrElse Vector.empty

}
