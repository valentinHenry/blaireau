// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.metas._
import shapeless.ops.hlist.{LeftReducer, Mapper}
import shapeless.{HList, Poly1, Poly2}
import skunk.util.Twiddler
import skunk.~

class BooleanEqApplier extends Poly1 with MetaFieldBooleanSyntax {
  implicit def field[A]: Case.Aux[ExtractedField[A], BooleanAction[A]] = at { case (field, elt) => field === elt }

  implicit def optionalField[A]: Case.Aux[ExtractedOptionalField[A], BooleanAction[Option[A]]] = at {
    case (field, elt) => field === elt
  }
}

object booleanEqAndApplier extends BooleanEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], BooleanAction[A]] = at { case (meta, elt) =>
    BooleanAction.booleanEqAnd(meta, elt)
  }

  implicit def optionalMeta[
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
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[Option[A], CO]
  ): Case.Aux[ExtractedOptionalMeta[A, MF, EF, IF, IMF, IEF], BooleanAction[Option[A]]] = at { case (meta, elt) =>
    BooleanAction.booleanEqAnd(meta, elt)
  }
}

object booleanEqOrApplier extends BooleanEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], BooleanAction[A]] = at { case (meta, elt) =>
    BooleanAction.booleanEqOr(meta, elt)
  }

  implicit def optionalMeta[
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
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[Option[A], CO]
  ): Case.Aux[ExtractedOptionalMeta[A, MF, EF, IF, IMF, IEF], BooleanAction[Option[A]]] = at { case (meta, elt) =>
    BooleanAction.booleanEqOr(meta, elt)
  }
}

class BooleanNEqApplier extends Poly1 with MetaFieldBooleanSyntax {
  implicit def field[A]: Case.Aux[ExtractedField[A], BooleanAction[A]] = at { case (field, elt) => field <> elt }

  implicit def optionalField[A]: Case.Aux[ExtractedOptionalField[A], BooleanAction[Option[A]]] = at {
    case (field, elt) => field <> elt
  }
}

object booleanNEqAndApplier extends BooleanNEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], BooleanAction[A]] = at { case (meta, elt) =>
    BooleanAction.booleanNEqAnd(meta, elt)
  }

  implicit def optionalMeta[
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
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[Option[A], CO]
  ): Case.Aux[ExtractedOptionalMeta[A, MF, EF, IF, IMF, IEF], BooleanAction[Option[A]]] = at { case (meta, elt) =>
    BooleanAction.booleanNEqAnd(meta, elt)
  }

}

object booleanNEqOrApplier extends BooleanNEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], BooleanAction[A]] = at { case (meta, elt) =>
    BooleanAction.booleanNEqOr(meta, elt)
  }

  implicit def optionalMeta[
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
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[Option[A], CO]
  ): Case.Aux[ExtractedOptionalMeta[A, MF, EF, IF, IMF, IEF], BooleanAction[Option[A]]] = at { case (meta, elt) =>
    BooleanAction.booleanNEqOr(meta, elt)
  }
}

object actionBooleanAndFolder extends Poly2 {
  implicit def folder[A, B]: Case.Aux[BooleanAction[A], BooleanAction[B], BooleanAction[A ~ B]] =
    at(_ && _)
}

object actionBooleanOrFolder extends Poly2 {
  implicit def folder[A, B]: Case.Aux[BooleanAction[A], BooleanAction[B], BooleanAction[A ~ B]] =
    at(_ || _)
}

object booleanNotEmptyApplier extends Poly1 with MetaFieldBooleanSyntax {
  implicit def optionalField[K, A]: Case.Aux[OptionalMetaField[A], BooleanAction[skunk.Void]] = at(_.isDefined)
}
