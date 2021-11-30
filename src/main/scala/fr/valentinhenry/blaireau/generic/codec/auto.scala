package fr.valentinhenry.blaireau.generic.codec

import fr.valentinhenry.blaireau.Meta
import fr.valentinhenry.blaireau.generic.MagnoliaMeta
import fr.valentinhenry.blaireau.metas.AllMetas
import magnolia.{CaseClass, Magnolia, SealedTrait}
import shapeless.Generic
import skunk.Codec

import scala.language.experimental.macros

object auto extends AllMetas {
  type Typeclass[T] = Meta[T]

  def combine[T: Generic](ctx: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaMeta.combine[T](ctx)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaMeta.dispatch[T](sealedTrait)

  implicit def deriveAuto[T]: Codec[T] = macro Magnolia.gen[T]
}
