package fr.valentinhenry.blaireau.generic

import fr.valentinhenry.blaireau.{Meta, MetaCodec}
import fr.valentinhenry.blaireau.metas.MetaCodec
import magnolia.{CaseClass, Magnolia, SealedTrait}

import scala.language.experimental.macros

object semiauto {
  type Typeclass[T] = Meta[T]

  def combine[T](ctx: CaseClass[Meta, T]): MetaCodec[T] =
    MagnoliaMeta.combine[T](ctx)

  def dispatch[T](sealedTrait: SealedTrait[Meta, T]): Meta[T] =
    MagnoliaMeta.dispatch[T](sealedTrait)

  def deriveCodec[T]: MetaCodec[T] = macro Magnolia.gen[T]
}
