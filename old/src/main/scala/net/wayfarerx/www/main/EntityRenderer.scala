package net.wayfarerx.www
package home

import cats.effect.IO
import fs2.Stream

trait EntityRenderer {
  self: Context with StructureRenderer with ContentRenderer =>

  /** The entity rendering strategy. */
  implicit final val entityRenderer: Renderer[Entity] = entity => {
    val stream = for {
      image <- findImage(entity)
      banners <- findBanners(entity)
    } yield {
      val metadata = Map[String, Data](
        "layout" -> entity.layout,
        "name" -> entity.name,
        "displayName" -> entity.displayName,
        "title" -> entity.title,
        "description" -> entity.description,
        "image" -> image.src,
        "image-alt" -> image.alt,
        "lead" -> Rendered(entity.lead),
        "path" -> entity.path,
        "location" -> entity.location
      ) ++ banners.flatMap { case (banner, bannerImage) =>
        Map[String, Data](
          s"banner-${banner.toString.toLowerCase}" -> bannerImage.src,
          s"banner-${banner.toString.toLowerCase}-alt" -> bannerImage.alt)
      } ++ entity.footer.toSeq.flatMap { footer =>
        Map[String, Data]("footer" -> Rendered(footer))
      } ++ entity.category.toSeq.flatMap { category =>
        Map[String, Data]("category" -> category)
      } ++ entity.style.map("style" -> Value(_)).toMap
      val (frontMatter, content): (Map[String, Data], Stream[IO, Content]) = entity match {
        case home: Home => Map("toc" -> componentsToTableOfContents(home.children)) ->
          Stream.empty
        case topic: Topic => Map("toc" -> componentsToTableOfContents(topic.children)) ->
          componentsToContent(topic.children)
        case article: Article => (
            article.headline.map("headline" -> Value(_)).toMap ++
            article.author.map("author" -> Value(_)).toMap
          ) -> Stream(Sequence(article.content))
      }
      Stream("---", NewLine) ++
        Structure(metadata ++ frontMatter).render ++
        Stream(NewLine, "---", NewLine) ++
        content.flatMap(_.render)
    }
    Stream eval stream flatMap identity
  }

  private def componentsToTableOfContents(components: Vector[Component]): Collection =
    Collection(components take 4 map
      (c => Structure("url" -> c.location, "title" -> c.title, "description" -> c.description)))

  private def componentsToContent(components: Vector[Component]): Stream[IO, Content] = {
    for {
      componentAndIndex <- Stream emits components.zipWithIndex
      (component, index) = componentAndIndex
      cls <- Stream(if (index % 2 == 0) "right" else "left")
      image <- Stream eval findImage(component)
    } yield section {
      val location = component.location
      div(header(h2(a(location, component.title))) ~ component.lead) ~ a(location, image, cls)
    } ~ NewLine
  } intersperse hr() ~ NewLine

  private def findImage(entity: Entity): IO[Image] = for {
    image <- findAsset(entity.image)
    result <- image map IO.pure getOrElse {
      entity match {
        case c: Component if c.parent.isDefined => findImage(c.parent.get)
        case _ => IO.pure(homePage.image)
      }
    }
  } yield result

  private def findBanners(entity: Entity): IO[Vector[(Entity.Banner, Image)]] = for {
    s <- findAsset(entity.banner(Entity.Banner.Small))
    m <- findAsset(entity.banner(Entity.Banner.Medium))
    l <- findAsset(entity.banner(Entity.Banner.Large))
    result <- {
      for {
        ss <- s
        mm <- m
        ll <- l
      } yield Vector(
        Entity.Banner.Small -> ss,
        Entity.Banner.Medium -> mm,
        Entity.Banner.Large -> ll)
    } map IO.pure getOrElse {
      entity match {
        case c: Component if c.parent.isDefined =>
          findBanners(c.parent.get)
        case _ => IO.pure(Vector(
          Entity.Banner.Small -> homePage.banner(Entity.Banner.Small),
          Entity.Banner.Medium -> homePage.banner(Entity.Banner.Medium),
          Entity.Banner.Large -> homePage.banner(Entity.Banner.Large)))
    }
    }
  } yield result

  private def findAsset(image: Image): IO[Option[Image]] = for {
    file <- File.attempt(targetDirectory.path.resolve(image.src substring 1))
  } yield file map (_ => image)

}