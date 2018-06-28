/*
 * package.scala
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
package site

import model._

/**
 * Global information about drinks.
 */
package object drinks {

  /** The type hints for entities in the drinks section. */
  val Hints: Site.Hints[Article] = Site.Hints(
    Site.Hints.Select("cocktails") -> Site.Hints[Article](Site.Hints[Cocktail]()),
    Site.Hints.Select("drinkware") -> Site.Hints[Article](Site.Hints[Component.Drinkware]()),
    Site.Hints.Select("equipment") -> Site.Hints[Article](Site.Hints[Component.Equipment]()),
    Site.Hints.Select("fruits") -> Site.Hints[Article](Site.Hints[Component.Fruit]()),
    Site.Hints.Select("mixers") -> Site.Hints[Article](Site.Hints[Component.Mixer]()),
    Site.Hints.Select("spirits") -> Site.Hints[Article](Site.Hints[Component.Spirit]()),
    Site.Hints.Select("wines") -> Site.Hints[Article](Site.Hints[Component.Wine]())
  )

}
