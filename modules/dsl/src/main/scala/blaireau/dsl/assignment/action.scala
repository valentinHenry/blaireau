// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.assignment

import blaireau.dsl.actions.{Action, FieldNamePicker, IMapper}
import blaireau.dsl.assignment
import blaireau.metas.MetaField
import blaireau.utils.FragmentUtils
import skunk.implicits.toStringOps
import skunk.{Codec, Fragment, Void, ~}

sealed trait AssignmentAction[A] extends Action[A] with Product with Serializable {
  self =>
  def <+>[B](right: AssignmentAction[B]): AssignmentAction[A ~ B] =
    assignment.ForgedAssignment(
      self.codec ~ right.codec,
      (self.elt, right.elt),
      picker => sql"${self.toFragment(picker)}, ${right.toFragment(picker)}"
    )
}

private final case class ForgedAssignment[A](codec: Codec[A], elt: A, fragment: FieldNamePicker => Fragment[A])
  extends AssignmentAction[A] {
  override def toFragment(picker: FieldNamePicker): Fragment[A] = fragment(picker)
}

object AssignmentAction {
  implicit def imapper: IMapper[AssignmentAction] = new IMapper[AssignmentAction] {
    override def imap[A, B](m: AssignmentAction[A])(f: A => B)(g: B => A): AssignmentAction[B] =
      ForgedAssignment(
        m.codec.imap(f)(g),
        f(m.elt),
        m.toFragment(_).contramap(g)
      )
  }

  private[blaireau] def empty: AssignmentAction[Void] = ForgedAssignment(Void.codec, Void, _ => Fragment.empty)

  private[blaireau] def none[A]: AssignmentAction[Option[A]] =
    imapper.imap(empty)(_ => cats.syntax.option.none[A])(_ => Void)

  case class AssignmentOp[A](field: MetaField[A], elt: A) extends Action.Op[A]("=", field) with AssignmentAction[A]

  case class AssignmentIncr[A](field: MetaField[A], elt: A) extends AssignmentAction[A] {
    override final val codec: Codec[A] = field.codec

    override def toFragment(picker: FieldNamePicker): Fragment[A] = {
      val fieldName = picker.get(field)
      FragmentUtils.withValue(s"$fieldName = $fieldName + ", codec)
    }
  }

  case class AssignmentDecr[A](field: MetaField[A], elt: A) extends AssignmentAction[A] {
    override final val codec: Codec[A] = field.codec

    override def toFragment(picker: FieldNamePicker): Fragment[A] = {
      val fieldName = picker.get(field)
      FragmentUtils.withValue(s"$fieldName = $fieldName - ", codec)
    }
  }
}
