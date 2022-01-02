// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.metas.MetaField
import blaireau.utils.FragmentUtils
import skunk.{Codec, Fragment}

trait Action[A] {
  self =>
  def codec: Codec[A]

  def elt: A

  def toFragment(picker: FieldNamePicker): Fragment[A]
}

object Action {
  abstract class Op[A](op: String, field: MetaField[A]) extends Action[A] {
    override def toFragment(picker: FieldNamePicker): Fragment[A] =
      FragmentUtils.withValue(s"${picker.get(field)} $op ", codec)

    override def codec: Codec[A] = field.codec
  }
}

trait IMapper[M[_]] {
  def imap[A, B](m: M[A])(f: A => B)(g: B => A): M[B]
}
