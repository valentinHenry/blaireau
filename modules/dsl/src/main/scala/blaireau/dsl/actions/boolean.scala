// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.dsl.syntax.MetaFieldBooleanSyntax
import blaireau.metas.Meta
import blaireau.metas.Meta.{ExtractedField, ExtractedMeta}
import shapeless.ops.hlist.{LeftReducer, Mapper}
import shapeless.{HList, Poly1, Poly2}
import skunk.implicits.{toIdOps, toStringOps}
import skunk.util.Twiddler
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
  implicit def imapper[A]: IMapper[BooleanAction, A] = new IMapper[BooleanAction, A] {
    override def imap[B](m: BooleanAction[A])(f: A => B)(g: B => A): BooleanAction[B] =
      ForgedBoolean(
        m.codec.imap(f)(g),
        f(m.elt),
        m.toFragment.contramap(g)
      )
  }

  def empty: BooleanAction[Void] = ForgedBoolean(
    Void.codec,
    Void,
    sql"TRUE"
  )

  private[blaireau] def booleanEqAnd[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanEqAndApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): BooleanAction[A] =
    Meta.applyFn[booleanEqAndApplier.type, actionBooleanAndFolder.type, A, F, MF, EF, MO, FO, CO, BooleanAction](
      meta,
      elt
    )

  private[blaireau] def booleanEqOr[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanEqOrApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): BooleanAction[A] =
    Meta.applyFn[booleanEqOrApplier.type, actionBooleanOrFolder.type, A, F, MF, EF, MO, FO, CO, BooleanAction](
      meta,
      elt
    )

  private[blaireau] def booleanNEqAnd[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanNEqAndApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): BooleanAction[A] =
    Meta.applyFn[booleanNEqAndApplier.type, actionBooleanAndFolder.type, A, F, MF, EF, MO, FO, CO, BooleanAction](
      meta,
      elt
    )

  private[blaireau] def booleanNEqOr[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanNEqOrApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): BooleanAction[A] =
    Meta.applyFn[booleanNEqOrApplier.type, actionBooleanOrFolder.type, A, F, MF, EF, MO, FO, CO, BooleanAction](
      meta,
      elt
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

class BooleanEqApplier extends Poly1 with MetaFieldBooleanSyntax {
  implicit def field[A]: Case.Aux[ExtractedField[A], BooleanAction[A]] = at { case (field, elt) => field === elt }
}

object booleanEqAndApplier extends BooleanEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], BooleanAction[A]] = at { case (field, elt) =>
    BooleanAction.booleanEqAnd(field, elt)
  }
}

object booleanEqOrApplier extends BooleanEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], BooleanAction[A]] = at { case (field, elt) =>
    BooleanAction.booleanEqOr(field, elt)
  }
}

class BooleanNEqApplier extends Poly1 with MetaFieldBooleanSyntax {
  implicit def field[A]: Case.Aux[ExtractedField[A], BooleanAction[A]] = at { case (field, elt) => field <> elt }
}

object booleanNEqAndApplier extends BooleanNEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], BooleanAction[A]] = at { case (field, elt) =>
    BooleanAction.booleanNEqAnd(field, elt)
  }
}

object booleanNEqOrApplier extends BooleanNEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF], BooleanAction[A]] = at { case (field, elt) =>
    BooleanAction.booleanNEqOr(field, elt)
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
