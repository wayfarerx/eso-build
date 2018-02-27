/*
 * Quantity.scala
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
package drinks

/**
  * Defines the amount of an ingredient that is used.
  *
  * @param amount The amount this quantity represents.
  * @param unit   The unit this quantity is measured in.
  */
case class Quantity(amount: Double, unit: Quantity.Unit) {

  /* Return the volume and unit suffix. */
  override def toString: String = s"${if (Math.floor(amount) == amount) amount.toLong else amount} $unit"

}

/**
  * Definitions of the various units of measure.
  */
object Quantity {

  //
  // Quantity factories.
  //

  /**
    * Attempts to decode a quantity from its string form.
    *
    * @param encoded The encoded quantity string.
    * @return The decoded quantity if it can be decoded.
    */
  def apply(encoded: String): Option[Quantity] = {
    val trimmed = encoded.trim
    trimmed indexWhere Character.isWhitespace match {
      case index if index > 0 => try {
        Unit(trimmed.substring(index + 1)) map (Quantity(trimmed.substring(0, index).toDouble, _))
      } catch {
        case _: NumberFormatException => None
      }
      case _ => None
    }
  }

  //
  // The supported units of volume.
  //

  /** The unit of volume equal to 1/1000th of a liter. */
  val Milliliters: Unit = Unit("mL", Name("milliliter", "milliliters"))

  /** The unit of volume equal to 1/100th of a liter. */
  val Centiliters: Unit = Unit("cL", Name("centiliter", "centiliters"))

  /** The unit of volume equal to 1/10th of a liter. */
  val Deciliters: Unit = Unit("dL", Name("deciliter", "deciliters"))

  /** The unit of volume equal to one liter. */
  val Liters: Unit = Unit("L", Name("liter", "liters"))

  /** The unit of volume equal to 1/32nd of a US fluid ounce. */
  val Dash: Unit = Unit("dash", Name("dash", "dashes"))

  /** The unit of volume equal to one US teaspoon. */
  val Teaspoon: Unit = Unit("tsp", Name("teaspoon", "teaspoons"))

  /** The unit of volume equal to one US tablespoon. */
  val Tablespoon: Unit = Unit("Tbsp", Name("tablespoon", "tablespoons"))

  /** The unit of volume equal to one US fluid ounce. */
  val FluidOunces: Unit = Unit("fl oz", Name("fluid ounce", "fluid ounces"))

  /** The unit of volume equal to one US cup. */
  val Cup: Unit = Unit("cp", Name("cup", "cups"))

  /** The unit of volume equal to one US pint. */
  val Pint: Unit = Unit("pt", Name("pint", "pints"))

  /** The unit of volume equal to one US quart. */
  val Quart: Unit = Unit("qt", Name("quart", "quarts"))

  /** The unit of volume equal to one US gallon. */
  val Gallon: Unit = Unit("gal", Name("gallon", "gallon"))

  //
  // The supported units of weight.
  //

  /** The unit of weight equal to 1/1000th of a gram. */
  val Milligrams: Unit = Unit("mg", Name("milligram", "milligrams"))

  /** The unit of weight equal to 1/100th of a gram. */
  val Centigrams: Unit = Unit("cg", Name("centigram", "centigrams"))

  /** The unit of weight equal to 1/10th of a gram. */
  val Decigrams: Unit = Unit("dg", Name("decigram", "decigrams"))

  /** The unit of weight equal to one gram. */
  val Grams: Unit = Unit("g", Name("gram", "grams"))

  /** The unit of weight equal to one US ounce. */
  val Ounces: Unit = Unit("oz", Name("ounce", "ounces"))

  /** The unit of weight equal to one US pound. */
  val Pounds: Unit = Unit("lb", Name("pound", "pounds"))

  //
  // The supported counting unit.
  //

  /** The unit measure equal to one item. */
  val Pieces: Unit = Unit("pcs", Name("piece", "pieces"))

  //
  // The unit type declaration & factory.
  //

  /**
    * Represents the unit that a quantity is measured in.
    *
    * @param abbreviation The abbreviation of this unit.
    * @param name         The name of this unit.
    */
  final class Unit private(val abbreviation: String, val name: Name) {

    /* Use value equality with the abbreviation, singular name and plural name. */
    override def equals(that: Any): Boolean = that match {
      case Unit(a, n) if a == abbreviation && n == name => true
      case _ => false
    }

    /* Use value equality with the abbreviation, singular name and plural name. */
    override def hashCode(): Int =
      Unit.hashCode ^ abbreviation.hashCode ^ name.hashCode

    /* Return the abbreviation. */
    override def toString: String = abbreviation

  }

  /**
    * Factory for units of measure.
    */
  object Unit {

    /** The units of measure indexed by abbreviation and name. */
    private lazy val index: Map[String, Unit] = Seq(
      Milliliters,
      Centiliters,
      Deciliters,
      Liters,
      Teaspoon,
      Tablespoon,
      FluidOunces,
      Cup,
      Pint,
      Quart,
      Gallon,
      Milligrams,
      Centigrams,
      Decigrams,
      Grams,
      Ounces,
      Pounds,
      Pieces
    ).flatMap { unit =>
      Seq(unit.abbreviation -> unit, unit.name.singular -> unit, unit.name.plural -> unit)
    }.map { case (key, value) =>
      key.toLowerCase -> value
    }.toMap

    /**
      * Looks up the specified unit by abbreviation or name.
      *
      * @param key The abbreviation, singular name or plural name of the unit to look up.
      * @return The requested unit of measure if it is found.
      */
    def apply(key: String): Option[Unit] =
      index get key.trim.replaceAll("""\s+""", " ").toLowerCase

    /**
      * Creates a new unit of measure.
      *
      * @param abbreviation The abbreviation of the unit.
      * @param name         The name of the unit.
      * @return A new unit of measure.
      */
    private[Quantity] def apply(abbreviation: String, name: Name): Unit =
      new Unit(abbreviation, name)

    /**
      * Extracts a unit of measure.
      *
      * @param unit The unit of measure to extract.
      * @return The abbreviation, singular name and plural name of the unit.
      */
    def unapply(unit: Unit): Option[(String, Name)] =
      Some(unit.abbreviation, unit.name)

  }

}
