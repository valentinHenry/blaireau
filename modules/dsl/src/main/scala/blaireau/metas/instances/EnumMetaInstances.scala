// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances

import blaireau.metas.{Meta, MetaS}
import enumeratum.values.{ValueEnum, ValueEnumEntry}
import enumeratum.{Enum, EnumEntry}
import skunk.codec.`enum`._
import skunk.data.Type

trait EnumMetaInstances {
  def enumMeta[A, E <: ValueEnumEntry[A]](
    `enum`: ValueEnum[A, E]
  )(implicit m: MetaS[A]): MetaS[ValueEnumEntry[A]] = {
    val codec = m.codec
      .eimap[ValueEnumEntry[A]](a => `enum`.withValueOpt(a).toRight(s"${`enum`}: no such element '$a'"))(_.value)

    Meta.of(codec)
  }

  def enumMeta[A <: EnumEntry](enumObj: Enum[A], tpe: Type): MetaS[A] =
    Meta.of(`enum`[A](enumObj, tpe))

  def enumMeta[A](encode: A => String, decode: String => Option[A], tpe: Type): MetaS[A] =
    Meta.of(`enum`(encode, decode, tpe))
}
