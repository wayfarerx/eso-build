package net.wayfarerx.www.eso.skills

sealed trait Skill

object Skill {

  sealed trait Active extends Skill

  object Active {

    sealed trait Basic extends Active {

      def morphs: (Morphed, Morphed)

    }

    sealed trait Morphed extends Active {

      def from: Basic

      final def other: Morphed = {
        ???
      }

    }

  }

  sealed trait Passive extends Skill

}
