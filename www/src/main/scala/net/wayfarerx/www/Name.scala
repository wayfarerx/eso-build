/*
 * Name.scala
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

/**
 * The name of an object in the system.
 */
sealed trait Name {

  /** The ID form of the name. */
  final lazy val id: String = distinct.replaceAll("""[^a-zA-Z0-9]+""", "-")

  /** The lowercase-only form of the name. */
  final lazy val distinct: String = casual.toLowerCase

  /** The casual form of the name for use in a sentence. */
  def casual: String

  /** The proper form of the name for use as a title. */
  def proper: String

  /* Equal to any identical names. */
  final override def equals(that: Any): Boolean = that match {
    case name: Name if name.casual == casual && name.proper == proper => true
    case _ => false
  }

  /* Hashes the type and both forms. */
  final override def hashCode(): Int =
    Name.hashCode ^ casual.hashCode ^ proper.hashCode

  /* Returns the casual form. */
  final override def toString: String =
    casual

}

/**
 * Factory for names.
 */
object Name {

  /**
   * Creates a name with the same casual and proper forms.
   *
   * @param name The name to use.
   * @return A name with the same casual and proper forms.
   */
  def apply(name: String): Name =
    Name(name, name)

  /**
   * Creates a name with the specified casual and proper forms.
   *
   * @param casual The casual form to use.
   * @param proper The proper form to use.
   * @return A name with the specified casual and proper forms.
   */
  def apply(casual: String, proper: String): Name = {
    val _casual = casual
    val _proper = proper
    new Name {

      override def casual: String = _casual

      override def proper: String = _proper

    }
  }

  /**
   * Provides extractor syntax for names.
   *
   * @param name The name to extract.
   * @return The contents of the name.
   */
  def unapply(name: Name): Option[(String, String, String, String)] =
    Some(name.id, name.distinct, name.casual, name.proper)

}
