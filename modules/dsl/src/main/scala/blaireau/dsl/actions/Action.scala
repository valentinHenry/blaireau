// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.utils.FragmentUtils
import cats.Id
import skunk.{Codec, Fragment, Void}

trait Action[A] { self =>
  def codec: Codec[A]
  def elt: A
  def toFragment: Fragment[A]
}

object Action {
  abstract class Op[A](op: String, fieldName: String) extends Action[A] {
    override def toFragment: Fragment[A] = FragmentUtils.withValue(s"$fieldName $op ", codec)
  }
}

trait IMapper[M[_], A] {
  def imap[B](m: M[A])(f: A => B)(g: B => A): M[B]
}
