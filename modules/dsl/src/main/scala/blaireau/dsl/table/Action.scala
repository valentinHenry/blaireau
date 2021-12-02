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

  sealed trait BooleanOp[A] extends Action[A] with Product with Serializable { self =>
    def &&[B](right: BooleanOp[B]): BooleanOp[A ~ B] =
      ForgedBoolean(
        self.codec ~ right.codec,
        (self.elt, right.elt),
        sql"(${self.toFragment} AND ${right.toFragment})"
      )

    def ||[B](right: BooleanOp[B]): BooleanOp[A ~ B] =
      ForgedBoolean(
        self.codec ~ right.codec,
        self.elt ~ right.elt,
        sql"(${self.toFragment} OR ${right.toFragment})"
      )
  }

  private[table] final case class ForgedBoolean[A](codec: Codec[A], elt: A, fragment: Fragment[A])
      extends BooleanOp[A] {
    override def toFragment: Fragment[A] = fragment
  }

  object BooleanOp {
    def empty: BooleanOp[Void] = ForgedBoolean(
      Void.codec,
      Void,
      sql"TRUE"
    )
  }

  final case class BooleanEq[A](sqlField: String, codec: Codec[A], elt: A)
      extends Op[A]("=", sqlField)
      with BooleanOp[A]

  final case class BooleanNEq[A](sqlField: String, codec: Codec[A], elt: A)
      extends Op[A]("<>", sqlField)
      with BooleanOp[A]
}
