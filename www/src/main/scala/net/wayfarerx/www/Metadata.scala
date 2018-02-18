/*
 * Metadata.scala
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

import language.implicitConversions

import io.circe.Json

/**
 * Base type for all nodes in the metadata tree.
 */
sealed trait Metadata {

  /**
   * Attempts to construct a value of the supplied type from this metadata.
   *
   * @tparam T The type of value to construct.
   * @return The resulting value if it can be constructed.
   */
  def as[T: Metadata.FromMetadata]: Option[T] =
    implicitly[Metadata.FromMetadata[T]].fromMetadata(this)

}

/**
 * Definition of the metadata tree.
 */
object Metadata {

  /**
   * Implicitly convert any JSON into metadata.
   *
   * @param json The JSON to convert to metadata.
   * @return The specified JSON converted to metadata.
   */
  implicit def jsonToMetadata(json: Json): Metadata =
    json.asObject map { o =>
      Structure(o.keys.flatMap(k => o(k) map jsonToMetadata map (k -> _)).toMap)
    } orElse json.asArray.map { a =>
      Collection(a map jsonToMetadata)
    } orElse json.asBoolean.map { b =>
      Value(b.toString)
    } orElse json.asNumber.map { n =>
      Value(n.toDouble.toString)
    } orElse json.asString.map { s =>
      Value(s)
    } getOrElse {
      Empty
    }

  /** The empty metadata. */
  val Empty: Structure = Structure(Map.empty)

  /**
   * A leaf node in the metadata tree.
   *
   * @param value The value of this leaf node.
   */
  case class Value(value: String) extends Metadata

  /**
   * A branch node in the metadata tree that contains a sequence of metadata items.
   *
   * @param collection The collection of metadata contained in this branch node.
   */
  case class Collection(collection: Vector[Metadata]) extends Metadata {

    /**
     * Attempts to construct a value of the supplied type from the metadata at the specified index.
     *
     * @tparam T The type of value to construct.
     * @param index The index of the metadata to construct a value from.
     * @return The resulting value if it can be constructed.
     */
    def apply[T: FromMetadata](index: Int): Option[T] =
      if (index < 0 || index >= collection.size) None else collection(index).as[T]

  }

  /**
   * A branch node in the metadata tree that contains a collection of named metadata items.
   *
   * @param structure The structure of metadata contained in this branch node.
   */
  case class Structure(structure: Map[String, Metadata]) extends Metadata {

    /**
     * Attempts to construct a value of the supplied type from the metadata with the specified key.
     *
     * @tparam T The type of value to construct.
     * @param key The key of the metadata to construct a value from.
     * @return The resulting value if it can be constructed.
     */
    def get[T: FromMetadata](key: String): Option[T] =
      structure get key flatMap (_.as[T])

  }

  /**
   * Type class that describes how a value can be constructed from metadata.
   *
   * @tparam T The type of value that is constructed.
   */
  trait FromMetadata[T] {

    /**
     * Attempts to construct a value from the specified metadata.
     *
     * @param metadata The metadata to construct the value from.
     * @return The resulting value if it can be constructed.
     */
    def fromMetadata(metadata: Metadata): Option[T]

  }

  /**
   * Common implicit constructors.
   */
  object FromMetadata {

    /** Convert values into booleans. */
    implicit val BooleanFromMetadata: FromMetadata[Boolean] = new Values[Boolean] {
      override protected def fromMetadataValue(value: Value): Option[Boolean] =
        value.value.toLowerCase match {
          case "true" => Some(true)
          case "false" => Some(false)
          case _ => None
        }
    }

    /** Convert values into bytes. */
    implicit val ByteFromMetadata: FromMetadata[Byte] = new Numeric[Byte] {
      override protected def parseMetadataValue(value: String): Byte = java.lang.Byte.parseByte(value)
    }

    /** Convert values into shorts. */
    implicit val ShortFromMetadata: FromMetadata[Short] = new Numeric[Short] {
      override protected def parseMetadataValue(value: String): Short = java.lang.Short.parseShort(value)
    }

    /** Convert values into integers. */
    implicit val IntFromMetadata: FromMetadata[Int] = new Numeric[Int] {
      override protected def parseMetadataValue(value: String): Int = java.lang.Integer.parseInt(value)
    }

    /** Convert values into floats. */
    implicit val FloatFromMetadata: FromMetadata[Float] = new Numeric[Float] {
      override protected def parseMetadataValue(value: String): Float = java.lang.Float.parseFloat(value)
    }

    /** Convert values into longs. */
    implicit val LongFromMetadata: FromMetadata[Long] = new Numeric[Long] {
      override protected def parseMetadataValue(value: String): Long = java.lang.Long.parseLong(value)
    }

    /** Convert values into doubles. */
    implicit val DoubleFromMetadata: FromMetadata[Double] = new Numeric[Double] {
      override protected def parseMetadataValue(value: String): Double = java.lang.Double.parseDouble(value)
    }

    /** Convert values into characters. */
    implicit val CharFromMetadata: FromMetadata[Char] = new Values[Char] {
      override protected def fromMetadataValue(value: Value): Option[Char] = value.value.length match {
        case 1 => Some(value.value(0))
        case _ => None
      }
    }

    /** Convert values into strings. */
    implicit val StringFromMetadata: FromMetadata[String] = new Values[String] {
      override protected def fromMetadataValue(value: Value): Option[String] = Some(value.value)
    }

    /**
     * Base type for constructions from metadata values.
     *
     * @tparam T The type of value that is constructed.
     */
    trait Values[T] extends FromMetadata[T] {

      /* Only construct from values. */
      final override def fromMetadata(metadata: Metadata): Option[T] = metadata match {
        case value@Value(_) => fromMetadataValue(value)
        case _ => None
      }

      /**
       * Attempts to construct a value from the supplied metadata value.
       *
       * @param value The metadata value to construct from.
       * @return The requested value if it can be constructed.
       */
      protected def fromMetadataValue(value: Value): Option[T]

    }

    /**
     * Base type for constructions from metadata values into numbers.
     *
     * @tparam T The type of value that is constructed.
     */
    trait Numeric[T] extends Values[T] {

      /* Only construct from values and catch format exceptions. */
      final override protected def fromMetadataValue(value: Value): Option[T] =
        try Some(parseMetadataValue(value.value)) catch {
          case _: NumberFormatException => None
        }

      /**
       * Constructs a value from the supplied text.
       *
       * @param value The text to construct from.
       * @return The requested value.
       */
      protected def parseMetadataValue(value: String): T

    }

  }

}
