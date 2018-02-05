/*
 * Asset.scala
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

package net.wayfarerx.www.generator

/**
 * Data of any type in a file.
 *
 * @param location The location of this asset in the website.
 * @param mimeType The MIME type of this asset.
 * @param contents A function that returns the contents of this asset.
 *
 */
case class Asset(location: String, mimeType: String)(val contents: () => Array[Byte])

