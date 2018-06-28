/*
 * Recipe.scala
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

import model._

import scala.util.{Failure, Success, Try}


/**
 * Represents a recipe.
 *
 * @param requirements The requirements for this recipe.
 * @param instructions The instructions for this recipe.
 */
case class Recipe(
  requirements: Vector[Recipe.Requirement],
  instructions: Vector[Markup]
)

/**
 * Definitions associated with recipes.
 */
object Recipe {

  /** The requirements header text. */
  val RequirementsHeader: String = "Requirements"

  /** The instructions header text. */
  val InstructionsHeader: String = "Instructions"

  /** The plural requirements class. */
  val RequirementsClass: String = RequirementsHeader.toLowerCase

  /** The plural instructions class. */
  val InstructionsClass: String = InstructionsHeader.toLowerCase()

  /** The singular requirement class. */
  val RequirementClass: String = RequirementsClass.substring(0, RequirementsClass.length - 1)

  /** The singular instruction class. */
  val InstructionClass: String = InstructionsHeader.substring(0, InstructionsClass.length - 1)

  /**
   * Attempts to decode a recipe.
   *
   * @param sections The sections to parse the recipe from.
   * @return The result of decoding the recipe and any remaining sections.
   */
  def decode(sections: Vector[Section]): Try[(Recipe, Vector[Section])] =
    sections match {
      case
        (Section(_, r, Markup.List.Unordered(reqItems, _, _), _, _, _)) +:
          (Section(_, i, Markup.List.Ordered(insItems, _, _), _, _, _)) +:
          remaining
        if r.strip.trim.equalsIgnoreCase(RequirementsHeader) && i.strip.trim.equalsIgnoreCase(InstructionsHeader) =>
        ((Success(Vector[Requirement]()): Try[Vector[Requirement]]) /: reqItems.map {
          case Markup.List.Item(Markup.Paragraph(nested, _, _), _, _) => nested
          case other => other.nested
        }) {
          (previous, next) =>
            previous flatMap (output => Requirement.extract(next) map {
              case (quantity, component, modifiers) => Success(output :+ Requirement(
                component.pointer.narrow[Component],
                component.title,
                component.nested,
                quantity flatMap (Quantity(_)) getOrElse Quantity(1, Quantity.Pieces),
                modifiers getOrElse Markup.empty
              ))
            } getOrElse Failure(new DecoderException.InvalidRequirement(s"Invalid recipe requirement: $next")))
        } map (requirements => Recipe(requirements, insItems map (_.nested)) -> remaining)
      case _ =>
        Failure(new DecoderException.NotFound("Recipe sections not found"))
    }

  /**
   * Attempts to encode a recipe.
   *
   * @param context The context to endode in.
   * @param recipe  The recipe to encode.
   * @param level   The level to encode at (defaults to 2).
   * @return The result of attempting to encode a recipe.
   */
  def encode(context: Context, recipe: Recipe, level: Int = 2): Try[Vector[Section]] =
    ((Success(Vector()): Try[Vector[Markup.List.Item]]) /: recipe.requirements) { (previous, requirement) =>
      for (items <- previous) yield items :+ Markup.List.Item(Markup(
        requirement.quantity match {
          case Quantity(1, Quantity.Pieces) => Markup.empty
          case q => Markup(Markup.Span(Markup(q.toString), q.flipped map (_.toString)), " ")
        },
        Markup.Link.Internal(requirement.component, None, None, requirement.nested),
        requirement.modifiers
      ), classes = Vector(RequirementClass))
    } map { requirements =>
      val instructions = recipe.instructions map (Markup.List.Item(_, classes = Vector(InstructionClass)))
      Vector(
        Section(level, RequirementsHeader, Markup.List.Unordered(requirements, classes = Vector(RequirementsClass))),
        Section(level, InstructionsHeader, Markup.List.Ordered(instructions, classes = Vector(InstructionsClass)))
      )
    }

  /**
   * A single requirement for a recipe.
   *
   * @param component A pointer to the required component.
   * @param title     The title of this requirement.
   * @param nested    The content nested in this requirement.
   * @param quantity  The quantity of the required component.
   * @param modifiers The optional qualifying text.
   */
  case class Requirement(
    component: Pointer[Component],
    title: Option[String],
    nested: Markup,
    quantity: Quantity = Quantity(1.0, Quantity.Pieces),
    modifiers: Markup = Markup.empty
  )

  /**
   * Factory for requirements.
   */
  object Requirement {

    /**
     * Attempts to extract the components of a requirement.
     *
     * @param markup The markup to extract from.
     * @return The extracted components.
     */
    def extract(markup: Markup): Option[(Option[String], Markup.Link.Internal[AnyRef], Option[Markup])] = markup match {
      case Markup.Sequence(Vector(Markup.Text(quantity), component@Markup.Link.Internal(_, _, _, _, _, _))) =>
        Some(Some(quantity), component, None)
      case Markup.Sequence(Markup.Text(quantity) +: (component@Markup.Link.Internal(_, _, _, _, _, _)) +: modifiers) =>
        Some((Some(quantity), component, Some(Markup(modifiers: _*))))
      case component@Markup.Link.Internal(_, _, _, _, _, _) =>
        Some(None, component, None)
      case Markup.Sequence((component@Markup.Link.Internal(_, _, _, _, _, _)) +: modifiers) =>
        Some(None, component, Some(Markup(modifiers: _*)))
      case _ => None
    }

  }

  /**
   * Represents an error decoding a recipe.
   *
   * @param msg The error message.
   */
  sealed abstract class DecoderException(msg: String) extends RuntimeException(msg)

  /**
   * Defines the decoder exceptions.
   */
  object DecoderException {

    /**
     * Represents an error decoding a requirement.
     *
     * @param msg The error message.
     */
    final class InvalidRequirement(msg: String) extends DecoderException(msg)

    /**
     * Represents an error finding a recipe.
     *
     * @param msg The error message.
     */
    final class NotFound(msg: String) extends DecoderException(msg)

  }

}