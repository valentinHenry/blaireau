// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.metas

import blaireau.{BlaireauConfiguration, Meta}
import blaireau.generic.MagnoliaMeta
import blaireau.metas.AllMetas
import magnolia.{CaseClass, Magnolia, SealedTrait}
import shapeless.Generic

import scala.language.experimental.macros

object auto extends AllMetas {
  type Typeclass[T] = Meta[T]

  def combine[T: Generic](ctx: CaseClass[Typeclass, T])(implicit config: BlaireauConfiguration): Typeclass[T] = {
    val meta = MagnoliaMeta.combine[T](ctx)
    tools.assertFieldsIntegrity(ctx.typeName.short, meta)
    meta
  }

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaMeta.dispatch[T](sealedTrait)

  implicit def deriveAuto[T]: Typeclass[T] = macro Magnolia.gen[T]
}
