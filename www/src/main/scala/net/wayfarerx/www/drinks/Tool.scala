package net.wayfarerx.www.drinks

sealed trait Tool {

}

object Tool {

  case object MixingGlass extends Tool

  case object Ice extends Tool

  case object Spoon extends Tool

  case object Strainer extends Tool

}
