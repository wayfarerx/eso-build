package net.wayfarerx.www.generator
package templates

import scalacss.DevDefaults._
import scalatags.Text.short._

trait MetadataTemplates {
  self: Website with stylesheets.InlineStyles =>

  private val ignore = Set("the", "and", "with")

  final def pageMetadata(page: Page, metadata: Metadata): Frag = frag(
    commonMetadata(metadata.title) ++
      descriptionMetadata(metadata.description) ++
      keywordMetadata(page.location, metadata) ++
      twitterMetadata(metadata.twitter) ++
      imageMetadata(page.image) ++
      stylesheetMetadata(page.backgrounds): _*
  )

  private def commonMetadata(title: String): Seq[Frag] = Seq(
    meta(*.charset := "utf-8"),
    meta(*.httpEquiv := "X-UA-Compatible", *.content := "IE=edge"),
    meta(*.name := "viewport", *.content := "width=device-width, initial-scale=1"),
    scalatags.Text.tags2.title(title),
    meta(*.name := "og:title", *.content := title),
    meta(*.name := "twitter:card", *.content := "summary"),
    meta(*.name := "twitter:site", *.content := "thewayfarerx")
  )

  private def descriptionMetadata(description: Option[String]): Seq[Frag] =
    description map { d =>
      Seq(
        meta(*.name := "description", *.content := d),
        meta(*.name := "og:description", *.content := d)
      )
    } getOrElse Seq()

  private def keywordMetadata(location: String, metadata: Metadata): Seq[Frag] = Seq(
    meta(*.name := "keywords", *.content := {
      Seq("wayfarer", "wayfarerx", "wayfarerx.net") ++ {
        location +: metadata.title +: metadata.description.toVector ++: metadata.keywords flatMap
          (_.split("""[\\\^\$\|\?\*/-_\s]+""")) filter (_.length > 2) filterNot (s => ignore(s.toLowerCase))
      }
    }.distinct mkString " ")
  )

  private def twitterMetadata(twitter: Option[String]): Seq[Frag] =
    Seq(meta(*.name := "twitter:creator", *.content := twitter getOrElse "thewayfarerx"))

  private def imageMetadata(image: Asset): Seq[Frag] =
    Seq(meta(*.name := "og:image", *.content := Server + image.location)) ++
      image.altText.map(alt => meta(*.name := "og:image:alt", *.content := alt)).toSeq

  private def stylesheetMetadata(backgrounds: Backgrounds): Seq[Frag] = Seq(
    link(*.rel := "stylesheet", *.href := "/css/wayfarerx.css"),
    scalatags.Text.tags2.style(new InlineCss(backgrounds).render.trim)
  )

}
