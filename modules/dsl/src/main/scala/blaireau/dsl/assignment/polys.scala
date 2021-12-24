package blaireau.dsl.assignment

import blaireau.metas._
import cats.implicits.catsSyntaxOptionId
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
  ): Case.Aux[ExtractedMeta[A, F, MF, EF, OEF], AssignmentAction[A]] = at { case (meta, elt) =>
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
    mapper: Mapper.Aux[this.type, IEF, MO],
    r: LeftReducer.Aux[MO, actionAssignmentFolder.type, FO],
    ev: FO =:= AssignmentAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedOptionalMeta[A, MF, EF, IF, IMF, IEF], AssignmentAction[Option[A]]] = at { case (meta, elt) =>
    elt match {
      case None => AssignmentAction.none[A]
      case Some(elt) =>
        AssignmentAction.imapper.imap(
          MetaUtils.applyExtract[this.type, actionAssignmentFolder.type, A, IEF, MO, FO, CO, AssignmentAction](
            elt,
            meta.internal.extract
          )
        )(_.some)(_.get)
    }
  }
}

object actionAssignmentFolder extends Poly2 {
  implicit def folder[A, B]: Case.Aux[AssignmentAction[A], AssignmentAction[B], AssignmentAction[A ~ B]] =
    at(_ <+> _)
}
