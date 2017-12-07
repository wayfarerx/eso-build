package net.wayfarerx.www

import net.wayfarerx.www.drinks.ingredients.Ingredient
import net.wayfarerx.www.drinks.cocktails.Cocktail

package object drinks {

  val Category = "drinks"

  val Dash = 0.061611519922

  implicit final class IngredientOps[T <: Ingredient](val ingredient: T) extends AnyVal {

    def apply(amount: T#Measure): Cocktail.Component[T] = Cocktail.Component(ingredient, amount)

  }

  implicit final class LiquidAmountsExtensions(val amount: Double) extends AnyVal {

    def cl: Amount.Liquid = centiliters

    def centiliter: Amount.Liquid = centiliters

    def centiliters: Amount.Liquid = Amount.Liquid(amount)

    def dash: Amount.Liquid = dashes

    def dashes: Amount.Liquid = Amount.Liquid(amount * Dash)

  }

  implicit final class SolidAmountsExtensions(val amount: Double) extends AnyVal {

    def gm: Amount.Solid = grams

    def gram: Amount.Solid = grams

    def grams: Amount.Solid = Amount.Solid(amount)

  }

  implicit final class UnitsAmountsExtensions(val amount: Double) extends AnyVal {

    def item: Amount.Units = items

    def items: Amount.Units = Amount.Units(amount)

  }

}
