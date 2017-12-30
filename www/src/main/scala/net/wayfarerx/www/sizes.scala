package net.wayfarerx.www

sealed trait Size {

  def larger: Option[Size] = None

  def smaller: Option[Size] = None

  def maximum: Option[Int] = None

  final def minimum: Option[Int] = smaller flatMap (_.maximum map (_ + 1))

}

object Size {

  def apply(): Vector[Size] = Vector(Tiny, Small, Medium, Large)

  sealed trait Layout extends Size

  object Layout {

    def apply(): Vector[Layout] = Vector(Tiny, Small, Medium)

  }

  sealed trait Resolution extends Size

  object Resolution {

    def apply(): Vector[Resolution] = Vector(Small, Medium, Large)

  }

}

case object Tiny extends Size.Layout {

  override def larger: Option[Size] = Some(Small)

  override def maximum: Option[Int] = Some(512)

}

case object Small extends Size.Layout with Size.Resolution {

  override def larger: Option[Size] = Some(Medium)

  override def smaller: Option[Size] = Some(Tiny)

  override def maximum: Option[Int] = Some(768)

}

case object Medium extends Size.Layout with Size.Resolution {

  override def larger: Option[Size] = Some(Large)

  override def smaller: Option[Size] = Some(Small)

  override def maximum: Option[Int] = Some(1536)

}

case object Large extends Size.Resolution {

  override def smaller: Option[Size] = Some(Medium)

}
