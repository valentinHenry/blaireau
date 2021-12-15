// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.utils.FragmentUtils
import skunk.{Codec, Fragment, Void}

trait Action[A] { self =>
  def codec: Codec[A]
  def elt: A
  def toFragment: Fragment[A]
}

object Action {
  def empty: Action[Void] = new Action[Void] {
    override def codec: Codec[Void]         = Void.codec
    override def elt: Void                  = Void
    override def toFragment: Fragment[Void] = Fragment.empty
  }

  abstract class Op[A](op: String, fieldName: String) extends Action[A] {
    override def toFragment: Fragment[A] = FragmentUtils.withValue(s"$fieldName $op ", codec)
  }
}
