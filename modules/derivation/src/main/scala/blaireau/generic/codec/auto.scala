// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec

import blaireau.{BlaireauConfiguration, Meta}
import blaireau.generic.MagnoliaMeta
import blaireau.metas.AllMetas
import magnolia.{CaseClass, Magnolia, SealedTrait}
import shapeless.Generic
import skunk.Codec

import scala.language.experimental.macros

object auto extends AllMetas {
  type Typeclass[T] = Meta[T]
  implicit val blaireauConfig: BlaireauConfiguration = BlaireauConfiguration.default

  def combine[T: Generic](ctx: CaseClass[Typeclass, T]): Codec[T] =
    MagnoliaMeta.combine[T](ctx).codec

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Codec[T] =
    MagnoliaMeta.dispatch[T](sealedTrait).codec

  implicit def codec[T]: Codec[T] = macro Magnolia.gen[T]
}
