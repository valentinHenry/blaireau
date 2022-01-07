// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.assignment

import blaireau.metas._
import shapeless.{HList, Poly1, Poly2}
import skunk.~

object assignmentApplier extends Poly1 with MetaFieldAssignmentSyntax {
  implicit def fieldAssignment[A]: Case.Aux[ExtractedField[A], AssignmentAction[A]] = at { case (field, elt) =>
    field := elt
  }

  implicit def optionalFieldAssignment[A]: Case.Aux[ExtractedOptionalField[A], AssignmentAction[Option[A]]] = at {
    case (field, elt) =>
      field := elt
  }

  implicit def metaAssignment[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](implicit
    ea: ExtractApplier[this.type, actionAssignmentFolder.type, A, EF, AssignmentAction]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], AssignmentAction[A]] = at { case (meta, elt) =>
    ea(meta.extract(elt))
  }

  implicit def optionalMetaAssignment[
    A,
    MF <: HList,
    EF <: HList,
    IF <: HList,
    IMF <: HList,
    IEF <: HList,
    MO <: HList,
    FO,
    CO
  ](implicit
    ea: ExtractApplier[this.type, actionAssignmentFolder.type, Option[A], EF, AssignmentAction]
  ): Case.Aux[ExtractedOptionalMeta[A, MF, EF, IF, IMF, IEF], AssignmentAction[Option[A]]] = at { case (meta, elt) =>
    ea(meta.extract(elt))
  }
}

object actionAssignmentFolder extends Poly2 {
  implicit def folder[A, B]: Case.Aux[AssignmentAction[A], AssignmentAction[B], AssignmentAction[A ~ B]] =
    at(_ <+> _)
}
