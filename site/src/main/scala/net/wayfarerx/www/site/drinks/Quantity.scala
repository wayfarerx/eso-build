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
package site.drinks

/**
 * Defines the amount of an ingredient that is used.
 *
 * @param amount The amount this quantity represents.
 * @param unit   The unit this quantity is measured in.
 */
case class Quantity(amount: Double, unit: Quantity.Unit[Quantity.Unit.Type]) {

  import Quantity._

  /**
   * Returns this quantity in the opposite unit if it is imperial or metric.
   *
   * @return This quantity in the opposite unit if it is imperial or metric.
   */
  def flipped: Option[Quantity] = unit.tpe match {
    case Unit.Imperial(_, _) => Some(toMetric)
    case Unit.Metric(_, _) => Some(toImperial)
    case _ => None
  }

  /**
   * Converts this quantity to imperial if it is metric.
   *
   * @return This quantity in imperial if it is metric.
   */
  def toImperial: Quantity = unit.tpe match {
    case Unit.Metric(imperialUnit, imperialFactor) => Quantity(amount * imperialFactor, imperialUnit)
    case _ => this
  }

  /**
   * Converts this quantity to metric if it is imperial.
   *
   * @return This quantity in metric if it is imperial.
   */
  def toMetric: Quantity = unit.tpe match {
    case Unit.Imperial(metricUnit, metricFactor) => Quantity(amount * metricFactor, metricUnit)
    case _ => this
  }

  /* Return the volume and unit suffix. */
  override def toString: String = amount match {
    case invalid if invalid <= 0 => s"0 $unit"
    case valid => s"${unit.tpe.render(valid)} $unit"
  }

}

/**
 * Definitions of the various units of measure.
 */
object Quantity {

  //
  // The supported units of volume.
  //

  /** The unit of volume equal to 1/1000th of a liter. */
  lazy val Milliliters: Unit[Unit.Metric] =
    Unit[Unit.Metric]("mL", "milliliter", "milliliters", Unit.Metric(Dash, 1.082048))

  /** The unit of volume equal to 1/100th of a liter. */
  lazy val Centiliters: Unit[Unit.Metric] =
    Unit[Unit.Metric]("cL", "centiliter", "centiliters", Unit.Metric(FluidOunces, 0.33814))

  /** The unit of volume equal to 1/10th of a liter. */
  lazy val Deciliters: Unit[Unit.Metric] =
    Unit[Unit.Metric]("dL", "deciliter", "deciliters", Unit.Metric(Cup, 0.422675))

  /** The unit of volume equal to one liter. */
  lazy val Liters: Unit[Unit.Metric] =
    Unit[Unit.Metric]("L", "liter", "liters", Unit.Metric(Quart, 1.05669))

  /** The unit of volume equal to 1/32nd of a US fluid ounce. */
  lazy val Dash: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("dash", "dash", "dashes", Unit.Imperial(Milliliters, 0.9241727988))

  /** The unit of volume equal to one US teaspoon. */
  lazy val Teaspoon: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("tsp", "teaspoon", "teaspoons", Unit.Imperial(Centiliters, 0.492892))

  /** The unit of volume equal to one US fluid ounce. */
  lazy val FluidOunces: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("fl oz", "fluid ounce", "fluid ounces", Unit.Imperial(Centiliters, 2.95735))

  /** The unit of volume equal to one US tablespoon. */
  lazy val Tablespoon: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("Tbsp", "tablespoon", "tablespoons", Unit.Imperial(Centiliters, 1.47868))

  /** The unit of volume equal to one US cup. */
  lazy val Cup: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("cup", "cup", "cups", Unit.Imperial(Deciliters, 2.36588))

  /** The unit of volume equal to one US pint. */
  lazy val Pint: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("pt", "pint", "pints", Unit.Imperial(Liters, 0.473176))

  /** The unit of volume equal to one US quart. */
  lazy val Quart: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("qt", "quart", "quarts", Unit.Imperial(Liters, 0.946353))

  /** The unit of volume equal to one US gallon. */
  lazy val Gallon: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("gal", "gallon", "gallon", Unit.Imperial(Liters, 3.78541))

  //
  // The supported units of weight.
  //

  /** The unit of weight equal to 1/1000th of a gram. */
  lazy val Milligrams: Unit[Unit.Metric] =
    Unit[Unit.Metric]("mg", "milligram", "milligrams", Unit.Metric(Ounces, 0.000035274))

  /** The unit of weight equal to 1/100th of a gram. */
  lazy val Centigrams: Unit[Unit.Metric] =
    Unit[Unit.Metric]("cg", "centigram", "centigrams", Unit.Metric(Ounces, 0.00035274))

  /** The unit of weight equal to 1/10th of a gram. */
  lazy val Decigrams: Unit[Unit.Metric] =
    Unit[Unit.Metric]("dg", "decigram", "decigrams", Unit.Metric(Ounces, 0.0035274))

  /** The unit of weight equal to one gram. */
  lazy val Grams: Unit[Unit.Metric] =
    Unit[Unit.Metric]("g", "gram", "grams", Unit.Metric(Ounces, 0.035274))

  /** The unit of weight equal to one US ounce. */
  lazy val Ounces: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("oz", "ounce", "ounces", Unit.Imperial(Grams, 28.3495))

  /** The unit of weight equal to one US pound. */
  lazy val Pounds: Unit[Unit.Imperial] =
    Unit[Unit.Imperial]("lb", "pound", "pounds", Unit.Imperial(Grams, 453.592))

  //
  // The supported counting unit.
  //

  /** The unit measure equal to one item. */
  lazy val Pieces: Unit[Unit.Count.type] =
    Unit[Unit.Count.type]("pcs", "piece", "pieces", Unit.Count)

  //
  // Internal data structures.
  //

  /** The characters that encode fractions. */
  private val Fractions = Vector(
    '\u215B', // 1/8
    '\u00BC', // 1/4
    '\u215C', // 3/8
    '\u00BD', // 1/2
    '\u215D', // 5/8
    '\u00BE', // 3/4
    '\u215E' // 7/8
  )

  /** The regex that extracts various formats of amounts. */
  private lazy val AmountRegex = (
    "([0-9]*)(" +
      """\.[0-9]*|""" +
      """\s+1\/8|""" +
      """\s+1\/4|""" +
      """\s+3\/8|""" +
      """\s+1\/2|""" +
      """\s+5\/8|""" +
      """\s+3\/4|""" +
      """\s+7\/8|""" +
      """\s+[""" +
      Fractions.mkString +
      "])?"
    ).r


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
      case index if index > 0 =>
        trimmed.substring(0, index) match {
          case AmountRegex(whole, fraction) =>
            try Unit(trimmed.substring(index + 1)) map { unit =>
              val wholeNumber = if (whole.isEmpty) 0.0 else whole.toDouble
              Option(fraction) map { f =>
                f.trim match {
                  case decimal if decimal startsWith "." =>
                    if (decimal.length == 1) Quantity(wholeNumber, unit)
                    else Quantity(wholeNumber + decimal.substring(1).toDouble, unit)
                  case "1/8" | "\u215B" =>
                    Quantity(wholeNumber + 1.0 / 8.0, unit)
                  case "1/4" | "\u00BC" =>
                    Quantity(wholeNumber + 1.0 / 4.0, unit)
                  case "3/8" | "\u215C" =>
                    Quantity(wholeNumber + 3.0 / 8.0, unit)
                  case "1/2" | "\u00BD" =>
                    Quantity(wholeNumber + 1.0 / 2.0, unit)
                  case "5/8" | "\u215D" =>
                    Quantity(wholeNumber + 5.0 / 8.0, unit)
                  case "3/4" | "\u00BE" =>
                    Quantity(wholeNumber + 3.0 / 4.0, unit)
                  case "7/8" | "\u215E" =>
                    Quantity(wholeNumber + 7.0 / 8.0, unit)
                  case _ =>
                    Quantity(wholeNumber, unit)
                }
              } getOrElse Quantity(wholeNumber, unit)
            } catch {
              case _: NumberFormatException => None
            }
          case _ => None
        }
      case _ => None
    }
  }

  //
  // The unit type declaration & factory.
  //

  /**
   * Represents the unit that a quantity is measured in.
   *
   * @param abbreviation The abbreviation of this unit.
   * @param singular     The singular name of this unit.
   * @param plural       The plural name of this unit.
   * @param typeFactory  The factory for the type of this unit.
   */
  final class Unit[+T <: Unit.Type] private[Quantity](
    val abbreviation: String,
    val singular: String,
    val plural: String,
    typeFactory: () => T
  ) {

    /** The type of this unit. */
    lazy val tpe: T = typeFactory()

    /* Return the abbreviation. */
    override def toString: String = abbreviation

  }

  /**
   * Factory for units of measure.
   */
  object Unit {

    /** The units of measure indexed by abbreviation and name. */
    private[Quantity] lazy val index: Map[String, Unit[Unit.Type]] = Seq(
      Milliliters,
      Centiliters,
      Deciliters,
      Liters,
      Dash,
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
      Seq(unit.abbreviation -> unit, unit.singular -> unit, unit.plural -> unit)
    }.map { case (key, value) =>
      key.toLowerCase -> value
    }.toMap

    /**
     * Looks up the specified unit by abbreviation or name.
     *
     * @param key The abbreviation, singular name or plural name of the unit to look up.
     * @return The requested unit of measure if it is found.
     */
    def apply(key: String): Option[Unit[Unit.Type]] =
      index get key.trim.replaceAll("""\s+""", " ").toLowerCase

    /**
     * Creates a unit that a quantity is measured in.
     *
     * @param abbreviation The abbreviation of this unit.
     * @param singular     The singular name of this unit.
     * @param plural       The plural name of this unit.
     * @param typeFactory  The factory for the type of this unit.
     */
    def apply[T <: Unit.Type](
      abbreviation: String,
      singular: String,
      plural: String,
      typeFactory: => T
    ): Unit[T] =
      new Unit[T](abbreviation, singular, plural, () => typeFactory)

    /**
     * Base type for unit types.
     */
    sealed trait Type {

      /**
       * Renders the specified amount according to this type's preference.
       *
       * @param amount The amount to render.
       * @return The rendered amount
       */
      def render(amount: Double): String =
        if (amount == Math.floor(amount)) amount.toLong.toString else {
          val granularity = 1 / ((Fractions.length + 1) * 2)
          val wholePart = Math.floor(amount).toLong
          amount - wholePart match {
            case down if down < granularity =>
              wholePart.toString
            case up if up >= 1 - granularity =>
              (wholePart + 1).toString
            case fractionalPart =>
              val fraction = Fractions(Math.floor((fractionalPart - granularity) * (Fractions.length + 1)).toInt)
              if (wholePart == 0) fraction.toString else wholePart.toString + fraction
          }
        }

    }

    /**
     * Defines an imperial unit.
     *
     * @param metricUnit   The closest metric unit.
     * @param metricFactor The factor to multiply by to get the metric amount.
     */
    case class Imperial(metricUnit: Unit[Metric], metricFactor: Double) extends Type

    /**
     * Defines a metric unit.
     *
     * @param imperialUnit   The closest imperial unit.
     * @param imperialFactor The factor to multiply by to get the imperial amount.
     */
    case class Metric(imperialUnit: Unit[Imperial], imperialFactor: Double) extends Type {

      /* Render the fractional part as an actual fraction. */
      override def render(amount: Double): String =
        if (amount == Math.floor(amount)) amount.toLong.toString else {
          val rounded = Math.round(amount * 100) / 100.0
          (rounded - (rounded % 0.01)).toString
        }

    }

    /**
     * Defines a unit that is counted.
     */
    case object Count extends Type

  }

}
