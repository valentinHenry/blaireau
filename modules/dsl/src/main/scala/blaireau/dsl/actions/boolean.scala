package blaireau.dsl.actions

import skunk.implicits.{toIdOps, toStringOps}
import skunk.{Codec, Fragment, Void, ~}

sealed trait BooleanAction[A] extends Action[A] with Product with Serializable { self =>
  def &&[B](right: BooleanAction[B]): BooleanAction[A ~ B] =
    ForgedBoolean(
      self.codec ~ right.codec,
      (self.elt, right.elt),
      sql"(${self.toFragment} AND ${right.toFragment})"
    )

  def ||[B](right: BooleanAction[B]): BooleanAction[A ~ B] =
    ForgedBoolean(
      self.codec ~ right.codec,
      self.elt ~ right.elt,
      sql"(${self.toFragment} OR ${right.toFragment})"
    )
}

private final case class ForgedBoolean[A](codec: Codec[A], elt: A, fragment: Fragment[A]) extends BooleanAction[A] {
  override def toFragment: Fragment[A] = fragment
}

object BooleanAction {
  def empty: BooleanAction[Void] = ForgedBoolean(
    Void.codec,
    Void,
    sql"TRUE"
  )

  final case class BooleanEq[A](sqlField: String, codec: Codec[A], elt: A)
      extends Action.Op[A]("=", sqlField)
      with BooleanAction[A]

  final case class BooleanLike[A](sqlField: String, codec: Codec[A], elt: A)
      extends Action.Op[A]("like", sqlField)
      with BooleanAction[A]

  final case class BooleanNEq[A](sqlField: String, codec: Codec[A], elt: A)
      extends Action.Op[A]("<>", sqlField)
      with BooleanAction[A]

  final case class BooleanGt[A](sqlField: String, codec: Codec[A], elt: A)
      extends Action.Op[A](">", sqlField)
      with BooleanAction[A]

  final case class BooleanGtEq[A](sqlField: String, codec: Codec[A], elt: A)
      extends Action.Op[A](">=", sqlField)
      with BooleanAction[A]

  final case class BooleanLt[A](sqlField: String, codec: Codec[A], elt: A)
      extends Action.Op[A]("<", sqlField)
      with BooleanAction[A]

  final case class BooleanLtEq[A](sqlField: String, codec: Codec[A], elt: A)
      extends Action.Op[A]("<", sqlField)
      with BooleanAction[A]
}
