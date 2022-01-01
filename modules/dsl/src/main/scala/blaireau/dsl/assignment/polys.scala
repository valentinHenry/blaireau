// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.assignment

import blaireau.metas._
import shapeless.ops.hlist.{LeftReducer, Mapper}
import shapeless.{HList, Poly1, Poly2}
import skunk.util.Twiddler
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
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionAssignmentFolder.type, FO],
    ev: FO =:= AssignmentAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], AssignmentAction[A]] = at { case (meta, elt) =>
    MetaUtils.applyExtract[this.type, actionAssignmentFolder.type, A, EF, MO, FO, CO, AssignmentAction](
      elt,
      meta.extract
    )
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
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionAssignmentFolder.type, FO],
    ev: FO =:= AssignmentAction[CO],
    tw: Twiddler.Aux[Option[A], CO]
  ): Case.Aux[ExtractedOptionalMeta[A, MF, EF, IF, IMF, IEF], AssignmentAction[Option[A]]] = at { case (meta, elt) =>
    MetaUtils.applyExtract[this.type, actionAssignmentFolder.type, Option[A], EF, MO, FO, CO, AssignmentAction](
      elt,
      meta.extract
    )
  }
}

object actionAssignmentFolder extends Poly2 {
  implicit def folder[A, B]: Case.Aux[AssignmentAction[A], AssignmentAction[B], AssignmentAction[A ~ B]] =
    at(_ <+> _)
}
