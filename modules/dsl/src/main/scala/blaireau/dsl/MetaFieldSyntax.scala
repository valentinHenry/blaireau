package blaireau.dsl

import blaireau.MetaField
import skunk.syntax.all._
import skunk.syntax.StringContextOps
import skunk.util.Origin
import skunk.{Codec, Fragment, ~}
import skunk.Void

import scala.language.implicitConversions

trait MetaFieldSyntax {
  implicit def toOps(f: MetaField): MetaFieldOps = new MetaFieldOps(f)
}

final class MetaFieldOps(val field: MetaField) {
  def ==(right: field.FieldType): Action.BooleanEq[field.FieldType] =
    Action.BooleanEq(field.sqlName, field.codec, right)

  def !=(right: field.FieldType): Action.BooleanNEq[field.FieldType] =
    Action.BooleanNEq(field.sqlName, field.codec, right)
}

sealed trait Action[A] { self =>
  def codec: Codec[A]
  def elt: A

  def toFragment: Fragment[A]

  def &&[B](right: Action[B]): Action[A ~ B] = new Action[A ~ B] {
    def codec: Codec[A ~ B] = self.codec ~ right.codec
    def elt: A ~ B          = (self.elt, right.elt)

    override def toFragment: Fragment[(A, B)] =
      sql"(${self.toFragment} AND ${right.toFragment})"
  }

  def ||[B](right: Action[B]): Action[A ~ B] = new Action[A ~ B] {
    override def codec: Codec[A ~ B] = self.codec ~ right.codec

    override def elt: A ~ B = self.elt ~ right.elt

    override def toFragment: Fragment[A ~ B] =
      sql"(${self.toFragment} OR ${right.toFragment})"
  }
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

  final case class BooleanEq[A](sqlField: String, codec: Codec[A], elt: A)  extends Op[A]("=", sqlField)
  final case class BooleanNEq[A](sqlField: String, codec: Codec[A], elt: A) extends Op[A]("<>", sqlField)
}
