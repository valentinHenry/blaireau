package fr.valentinhenry.blaireau.generic

import fr.valentinhenry.blaireau.{Meta, MetaCodec}
import fr.valentinhenry.blaireau.metas.AllMetas
import magnolia.{CaseClass, Magnolia, SealedTrait}

import scala.language.experimental.macros

object auto extends AllMetas {
  type Typeclass[T] = Meta[T]

  def combine[T](ctx: CaseClass[Meta, T]): MetaCodec[T] =
    MagnoliaMeta.combine[T](ctx)

  def dispatch[T](sealedTrait: SealedTrait[Meta, T]): Meta[T] =
    MagnoliaMeta.dispatch[T](sealedTrait)

  implicit def deriveAuto[T]: MetaCodec[T] = macro Magnolia.gen[T]
}
