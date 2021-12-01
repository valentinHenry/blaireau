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

//TODO do not use MagnoliaMeta
object auto extends AllMetas {
  type Typeclass[T] = Meta[T]
  implicit val blaireauConfig: BlaireauConfiguration = BlaireauConfiguration.default

  def combine[T: Generic](ctx: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaMeta.combine[T](ctx)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaMeta.dispatch[T](sealedTrait)

  private[blaireau] implicit def meta[T]: Typeclass[T] = macro Magnolia.gen[T]

  implicit def codec[T: Meta]: Codec[T] = Meta[T].codec
}
