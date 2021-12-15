// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.utils.FragmentUtils
import skunk.implicits.toStringOps
import skunk.{Codec, Fragment, Void, ~}

sealed trait AssignmentAction[A] extends Action[A] with Product with Serializable { self =>
  def <+>[B](right: AssignmentAction[B]): AssignmentAction[A ~ B] =
    ForgedAssignment(
      self.codec ~ right.codec,
      (self.elt, right.elt),
      sql"${self.toFragment}, ${right.toFragment}"
    )
}

private final case class ForgedAssignment[A](codec: Codec[A], elt: A, fragment: Fragment[A])
    extends AssignmentAction[A] {
  override def toFragment: Fragment[A] = fragment
}

object AssignmentAction {
  case class AssignmentOp[A](sqlField: String, codec: Codec[A], elt: A)
      extends Action.Op[A]("=", sqlField)
      with AssignmentAction[A]

  case class AssignmentIncr[A](sqlField: String, codec: Codec[A], elt: A) extends AssignmentAction[A] {
    override def toFragment: Fragment[A] = FragmentUtils.withValue(s"$sqlField = $sqlField + ", codec)
  }

  case class AssignmentDecr[A](sqlField: String, codec: Codec[A], elt: A) extends AssignmentAction[A] {
    override def toFragment: Fragment[A] = FragmentUtils.withValue(s"$sqlField = $sqlField - ", codec)
  }
}
