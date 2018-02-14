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
 * @tparam UnitType The type of unit this quantity is measured in.
 * @param value The amount this quantity represents.
 * @param unit  The unit this quantity is measured in.
 */
case class Quantity[+UnitType <: Quantity.Unit](value: Double, unit: UnitType) {

  def unary_+ : Quantity[UnitType] = this

  def unary_- : Quantity[UnitType] = Quantity(-value, unit)

  def + (that: Double): Quantity[UnitType] = this :+ that

  def - (that: Double): Quantity[UnitType] = this :- that

  def * (that: Double): Quantity[UnitType] = this :* that

  def / (that: Double): Quantity[UnitType] = this :/ that

  def :+ (that: Double): Quantity[UnitType] = Quantity(value + that, unit)

  def :- (that: Double): Quantity[UnitType] = Quantity(value - that, unit)

  def :* (that: Double): Quantity[UnitType] = Quantity(value * that, unit)

  def :/ (that: Double): Quantity[UnitType] = Quantity(value / that, unit)

  /* Return the volume and unit suffix. */
  override def toString: String = s"$value $unit"

}

/**
 * Definitions of the various units of measure.
 */
object Quantity {

  //
  // Aliases and factories for common quantities.
  //

  /** A quantity measured by volume. */
  type Volume = Quantity[ByVolume]

  /**
   * Factory for quantities by volume.
   */
  object Volume {

    /**
     * Creates a new quantity measured by volume.
     *
     * @param value The amount this quantity represents.
     * @param unit  The volume this quantity is measured in.
     * @return A new quantity measured by volume.
     */
    def apply(value: Double, unit: ByVolume): Volume = Quantity(value, unit)

  }

  /** A quantity measured by weight. */
  type Weight = Quantity[ByWeight]

  /**
   * Factory for quantities by weight.
   */
  object Weight {

    /**
     * Creates a new quantity measured by weight.
     *
     * @param value The amount this quantity represents.
     * @param unit  The weight this quantity is measured in.
     * @return A new quantity measured by weight.
     */
    def apply(value: Double, unit: ByWeight): Weight = Quantity(value, unit)

  }

  //
  // Implicit interfaces available on quantities.
  //

  implicit class VolumeExtensions(val volume: Volume) extends AnyVal {

    def toMl: Volume = toMilliliters

    def toCl: Volume = toCentiliters

    def toDl: Volume = toDeciliters

    def toML: Volume = toMilliliters

    def toCL: Volume = toCentiliters

    def toDL: Volume = toDeciliters

    def toL: Volume = toLiters

    def toMilliliters: Volume = Volume(volume.unit.convertTo(volume.value, Milliliters), Milliliters)

    def toCentiliters: Volume = Volume(volume.unit.convertTo(volume.value, Centiliters), Centiliters)

    def toDeciliters: Volume = Volume(volume.unit.convertTo(volume.value, Deciliters), Deciliters)

    def toLiters: Volume = Volume(volume.unit.convertTo(volume.value, Liters), Liters)

  }

  //
  // The supported units of volume.
  //

  /** The unit of volume equal to 1/1000th of a liter. */
  lazy val Milliliters: ByVolume = new ByVolume {

    /* Always defer to other measures of volume. */
    override def convertFrom(value: Double, unit: ByVolume): Double = unit match {
      case Milliliters => value
      case other => other.convertTo(value, Milliliters)
    }

    /* Always defer to other measures of volume. */
    override def convertTo(value: Double, unit: ByVolume): Double = unit match {
      case Milliliters => value
      case other => other.convertFrom(value, Milliliters)
    }

    /* Return the milliliter suffix. */
    override def toString: String = "ml"

  }

  /** The unit of volume equal to 1/100th of a liter. */
  lazy val Centiliters: ByVolume = ByVolume(10, "cl")

  /** The unit of volume equal to 1/10th of a liter. */
  lazy val Deciliters: ByVolume = ByVolume(100, "dl")

  /** The unit of volume equal to one liter. */
  lazy val Liters: ByVolume = ByVolume(1000, "l")

  //
  // The supported units of weight.
  //

  /** The unit of weight equal to 1/1000th of a gram. */
  lazy val Milligrams: ByWeight = new ByWeight {

    /* Always defer to other measures of weight. */
    override def convertFrom(value: Double, unit: ByWeight): Double = unit match {
      case Milligrams => value
      case other => other.convertTo(value, Milligrams)
    }

    /* Always defer to other measures of weight. */
    override def convertTo(value: Double, unit: ByWeight): Double = unit match {
      case Milligrams => value
      case other => other.convertFrom(value, Milligrams)
    }

    /* Return the milligram suffix. */
    override def toString: String = "mg"

  }

  /** The unit of weight equal to 1/100th of a gram. */
  lazy val Centigrams: ByWeight = ByWeight(10, "cg")

  /** The unit of weight equal to 1/10th of a gram. */
  lazy val Decigrams: ByWeight = ByWeight(100, "dg")

  /** The unit of weight equal to one gram. */
  lazy val Grams: ByWeight = ByWeight(1000, "g")


  //
  // Definitions of and factories for the unit hierarchy.
  //

  /**
   * Base type for all units of measure.
   */
  sealed trait Unit {

    /** The class of units that this unit can be converted to and from. */
    type CompatibleUnit <: Unit

    /**
     * Returns the specified value in the supplied unit converted to a value in this unit.
     *
     * @param value The value in the supplied unit to convert.
     * @param unit  The unit to convert the value from.
     * @return The specified value in the supplied unit converted to a value in this unit.
     */
    def convertFrom(value: Double, unit: CompatibleUnit): Double

    /**
     * Returns the specified value in this unit converted to a value in the specified unit.
     *
     * @param value The value in this unit to convert.
     * @param unit  The unit to convert the value to.
     * @return The specified value in this unit converted to a value in the specified unit.
     */
    def convertTo(value: Double, unit: CompatibleUnit): Double

  }

  /**
   * Base type for units of measure by volume.
   */
  sealed trait ByVolume extends Unit {

    /* Compatible with any volume. */
    final override type CompatibleUnit = ByVolume

  }

  /**
   * Factory for units of measure by volume.
   */
  object ByVolume {

    /**
     * Creates a new measure of volume from the equivalent number of milliliters and a suffix.
     *
     * @param inMilliliters The number of milliliters in the unit.
     * @param suffix        The suffix of the unit.
     * @return A new measure of volume from the equivalent number of milliliters and a suffix.
     */
    def apply(inMilliliters: Double, suffix: String): ByVolume = new ByVolume {

      /* Convert through milliliters. */
      override def convertFrom(value: Double, unit: ByVolume): Double = unit match {
        case Milliliters => value / inMilliliters
        case other => convertFrom(other.convertTo(value, Milliliters), Milliliters)
      }

      /* Convert milliliters or defer to the other unit. */
      override def convertTo(value: Double, unit: ByVolume): Double = unit match {
        case Milliliters => value * inMilliliters
        case other => other.convertFrom(value, this)
      }

    }

  }

  /**
   * Base type for units of measure by weight.
   */
  sealed trait ByWeight extends Unit {

    /* Compatible with any weight. */
    final override type CompatibleUnit = ByWeight

  }

  /**
   * Factory for units of measure by weight.
   */
  object ByWeight {

    /**
     * Creates a new measure of weight from the equivalent number of milligrams and a suffix.
     *
     * @param inMilligrams The number of milligrams in the unit.
     * @param suffix       The suffix of the unit.
     * @return A new measure of weight from the equivalent number of milligrams and a suffix.
     */
    def apply(inMilligrams: Double, suffix: String): ByWeight = new ByWeight {

      /* Convert through milliliters. */
      override def convertFrom(value: Double, unit: ByWeight): Double = unit match {
        case Milligrams => value / inMilligrams
        case other => convertFrom(other.convertTo(value, Milligrams), Milligrams)
      }

      /* Convert milliliters or defer to the other unit. */
      override def convertTo(value: Double, unit: ByWeight): Double = unit match {
        case Milligrams => value * inMilligrams
        case other => other.convertFrom(value, this)
      }

    }

  }

}
