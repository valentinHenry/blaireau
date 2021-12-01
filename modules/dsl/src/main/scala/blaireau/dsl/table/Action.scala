// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.table

import skunk.syntax.StringContextOps
import skunk.syntax.all._
import skunk.util.Origin
import skunk.{Codec, Fragment, Void, ~}

sealed trait Action[A] { self =>
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

  sealed abstract class Op[A](op: String, fieldName: String) extends Action[A] {
    override def toFragment: Fragment[A] =
      StringContextOps.fragmentFromParts(
        List(
          StringContextOps.Str(s"$fieldName $op"),
          StringContextOps.Par(codec.sql)
        ),
        codec,
        Origin.unknown
      )
  }

  sealed trait BooleanOp[A] extends Action[A] { self =>
    def &&[B](right: BooleanOp[B]): BooleanOp[A ~ B] = new BooleanOp[A ~ B] {
      def codec: Codec[A ~ B] = self.codec ~ right.codec
      def elt: A ~ B          = (self.elt, right.elt)

      override def toFragment: Fragment[(A, B)] =
        sql"(${self.toFragment} AND ${right.toFragment})"
    }

    def ||[B](right: BooleanOp[B]): BooleanOp[A ~ B] = new BooleanOp[A ~ B] {
      override def codec: Codec[A ~ B] = self.codec ~ right.codec

      override def elt: A ~ B = self.elt ~ right.elt

      override def toFragment: Fragment[A ~ B] =
        sql"(${self.toFragment} OR ${right.toFragment})"
    }
  }

  object BooleanOp {
    def empty: BooleanOp[Void] = new BooleanOp[Void] {
      override def codec: Codec[Void]         = Void.codec
      override def elt: Void                  = Void
      override def toFragment: Fragment[Void] = sql"TRUE"
    }
  }

  final case class BooleanEq[A](sqlField: String, codec: Codec[A], elt: A)
      extends Op[A]("=", sqlField)
      with BooleanOp[A]

  final case class BooleanNEq[A](sqlField: String, codec: Codec[A], elt: A)
      extends Op[A]("<>", sqlField)
      with BooleanOp[A]
}
