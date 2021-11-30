package fr.valentinhenry.blaireau.generic.metas

import fr.valentinhenry.blaireau.Meta
import fr.valentinhenry.blaireau.generic.MagnoliaMeta
import magnolia.{CaseClass, Magnolia, SealedTrait}
import shapeless.Generic

import scala.language.experimental.macros

object semiauto {
  type Typeclass[T] = Meta[T]

  def combine[T: Generic](ctx: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaMeta.combine[T](ctx)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaMeta.dispatch[T](sealedTrait)

  def deriveCodec[T]: Typeclass[T] = macro Magnolia.gen[T]
}
