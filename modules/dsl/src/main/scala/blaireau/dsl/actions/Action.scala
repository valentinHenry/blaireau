// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.utils.FragmentUtils
import skunk.{Codec, Fragment}

trait Action[C, A] { self =>
  def codec: Codec[C]
  def elt: A

  def to(a: A): C

  def toFragment: Fragment[C]
}

object Action {
  abstract class Op[A](op: String, fieldName: String) extends Action[A, A] {
    override def toFragment: Fragment[A] = FragmentUtils.withValue(s"$fieldName $op ", codec)
    override def to(a: A): A             = a
  }
}

trait IMapper[M[_], A] {
  def imap[B](m: M[A])(f: A => B)(g: B => A): M[B]
}
