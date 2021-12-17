// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.dsl.syntax.MetaFieldAssignmentSyntax
import blaireau.metas.Meta
import blaireau.metas.Meta.{ExtractedField, ExtractedMeta}
import blaireau.utils.FragmentUtils
import shapeless.ops.hlist.{LeftReducer, Mapper}
import shapeless.{HList, Poly1, Poly2}
import skunk.implicits.toStringOps
import skunk.util.Twiddler
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
  implicit def imapper[A]: IMapper[AssignmentAction, A] = new IMapper[AssignmentAction, A] {
    override def imap[B](m: AssignmentAction[A])(f: A => B)(g: B => A): AssignmentAction[B] =
      ForgedAssignment(
        m.codec.imap(f)(g),
        f(m.elt),
        m.toFragment.contramap(g)
      )
  }

  private[blaireau] def assignMeta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[assignmentApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionAssignmentFolder.type, FO],
    ev: FO =:= AssignmentAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): AssignmentAction[A] =
    Meta.applyFn[assignmentApplier.type, actionAssignmentFolder.type, A, F, MF, EF, MO, FO, CO, AssignmentAction](
      meta,
      elt
    )

  private[blaireau] def empty: AssignmentAction[Void] = ForgedAssignment(Void.codec, Void, Fragment.empty)

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

object assignmentApplier extends Poly1 with MetaFieldAssignmentSyntax {
  implicit def fieldAssignment[A]: Case.Aux[ExtractedField[A], AssignmentAction[A]] = at { case (field, elt) =>
    field := elt
  }

  implicit def metaAssignment[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionAssignmentFolder.type, FO],
    ev: FO =:= AssignmentAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], AssignmentAction[A]] = at { case (meta, elt) =>
    AssignmentAction.assignMeta(meta, elt)
  }
}

object actionAssignmentFolder extends Poly2 {
  implicit def folder[A, B]: Case.Aux[AssignmentAction[A], AssignmentAction[B], AssignmentAction[A ~ B]] =
    at(_ <+> _)
}
