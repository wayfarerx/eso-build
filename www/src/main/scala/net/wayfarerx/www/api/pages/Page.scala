package net.wayfarerx.www.api
package pages

abstract class Page[T: Page.Support] private[pages] {

  protected val page: T

  final def title: String = implicitly[Page.Support[T]].title(page)

}

object Page {

  trait Support[T] {

    def title(page: T): String

  }

}
