// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec

import blaireau.generic.codec.instances.AllCodecInstances
import magnolia.{CaseClass, Magnolia, SealedTrait}
import _root_.shapeless.Generic
import skunk.Codec

import scala.language.experimental.macros

object auto extends AllCodecInstances {
  type Typeclass[T] = Codec[T]
  def combine[T: Generic](ctx: CaseClass[Typeclass, T]): Typeclass[T] =
    MagnoliaCodec.combine[T](ctx)

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaCodec.dispatch[T](sealedTrait)

  implicit def codec[T]: Codec[T] = macro Magnolia.gen[T]
}
