// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.dsl.actions.{Action, IMapper}
import blaireau.dsl.filtering
import blaireau.metas.{ExtractedField, ExtractedMeta, Meta}
import blaireau.utils.FragmentUtils
import shapeless.ops.hlist.{LeftReducer, Mapper}
import shapeless.{HList, Poly1, Poly2}
import skunk.implicits.{toIdOps, toStringOps}
import skunk.util.Twiddler
import skunk.{Codec, Fragment, Void, ~}

sealed trait BooleanAction[AC, A] extends Action[AC, A] with Product with Serializable { self =>
  def &&[BC, B](right: BooleanAction[BC, B]): BooleanAction[AC ~ BC, A ~ B] =
    filtering.ForgedBoolean(
      self.codec ~ right.codec,
      (self.elt, right.elt),
      sql"(${self.toFragment} AND ${right.toFragment})",
      ab => self.to(ab._1) ~ right.to(ab._2)
    )

  def ||[BC, B](right: BooleanAction[BC, B]): BooleanAction[AC ~ BC, A ~ B] =
    filtering.ForgedBoolean(
      self.codec ~ right.codec,
      self.elt ~ right.elt,
      sql"(${self.toFragment} OR ${right.toFragment})",
      ab => self.to(ab._1) ~ right.to(ab._2)
    )
}

private[blaireau] final case class ForgedBoolean[C, A](
  codec: Codec[C],
  elt: A,
  fragment: Fragment[C],
  to0: A => C
) extends BooleanAction[C, A] {
  override def toFragment: Fragment[C] = fragment
  override def to(a: A): C             = to0(a)
}

object BooleanAction {
  implicit def imapper[A]: IMapper[IdBooleanAction, A] = new IMapper[IdBooleanAction, A] {
    override def imap[B](m: IdBooleanAction[A])(f: A => B)(g: B => A): IdBooleanAction[B] =
      BooleanAction.imap(m)(f)(f)(g)(identity)
  }

  def imap[AC, A, BC, B](
    m: BooleanAction[AC, A]
  )(f: A => B)(fc: AC => BC)(gc: BC => AC)(toC: B => BC): BooleanAction[BC, B] =
    ForgedBoolean(
      m.codec.imap(fc)(gc),
      f(m.elt),
      m.toFragment.contramap(gc),
      toC
    )

  trait VoidBooleanAction extends IdBooleanAction[Void] {
    final override def to(a: Void): Void  = a
    final override val codec: Codec[Void] = Void.codec
    final override def elt: Void          = Void
  }

  def empty: IdBooleanAction[Void] = ForgedBoolean(
    Void.codec,
    Void,
    sql"TRUE",
    identity
  )

  private[blaireau] def booleanEqAnd[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF, OEF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanEqAndApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= IdBooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): IdBooleanAction[A] =
    Meta.applyFn[booleanEqAndApplier.type, actionBooleanAndFolder.type, A, F, MF, EF, OEF, MO, FO, CO, IdBooleanAction](
      meta,
      elt
    )

  private[blaireau] def booleanEqOr[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF, OEF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanEqOrApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= IdBooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): IdBooleanAction[A] =
    Meta.applyFn[booleanEqOrApplier.type, actionBooleanOrFolder.type, A, F, MF, EF, OEF, MO, FO, CO, IdBooleanAction](
      meta,
      elt
    )

  private[blaireau] def booleanNEqAnd[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF, OEF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanNEqAndApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= IdBooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): IdBooleanAction[A] =
    Meta
      .applyFn[booleanNEqAndApplier.type, actionBooleanAndFolder.type, A, F, MF, EF, OEF, MO, FO, CO, IdBooleanAction](
        meta,
        elt
      )

  private[blaireau] def booleanNEqOr[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](
    meta: Meta.Aux[A, F, MF, EF, OEF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanNEqOrApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= IdBooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): IdBooleanAction[A] =
    Meta.applyFn[booleanNEqOrApplier.type, actionBooleanOrFolder.type, A, F, MF, EF, OEF, MO, FO, CO, IdBooleanAction](
      meta,
      elt
    )

  final case class BooleanEq[A](sqlField: String, codec: Codec[A], elt: A)
    extends Action.Op[A]("=", sqlField)
    with IdBooleanAction[A]

  final case class BooleanLike[A](sqlField: String, codec: Codec[A], elt: A)
    extends Action.Op[A]("like", sqlField)
    with IdBooleanAction[A]

  final case class BooleanNEq[A](sqlField: String, codec: Codec[A], elt: A)
    extends Action.Op[A]("<>", sqlField)
    with IdBooleanAction[A]

  final case class BooleanGt[A](sqlField: String, codec: Codec[A], elt: A)
    extends Action.Op[A](">", sqlField)
    with IdBooleanAction[A]

  final case class BooleanGtEq[A](sqlField: String, codec: Codec[A], elt: A)
    extends Action.Op[A](">=", sqlField)
    with IdBooleanAction[A]

  final case class BooleanLt[A](sqlField: String, codec: Codec[A], elt: A)
    extends Action.Op[A]("<", sqlField)
    with IdBooleanAction[A]

  final case class BooleanLtEq[A](sqlField: String, codec: Codec[A], elt: A)
    extends Action.Op[A]("<=", sqlField)
    with IdBooleanAction[A]

  final case class BooleanOptionIsEmpty(sqlField: String) extends VoidBooleanAction {
    override def toFragment: Fragment[Void] = FragmentUtils.const(s"$sqlField IS NULL")
  }

  final case class BooleanOptionIsDefined(sqlField: String) extends VoidBooleanAction {
    override def toFragment: Fragment[Void] = FragmentUtils.const(s"$sqlField IS NOT NULL")
  }

  final case class BooleanOptionContains[A](sqlField: String, codec: Codec[Option[A]], elt: A)
    extends BooleanAction[Option[A], A] {
    override def to(a: A): Option[A] = Some(a)
    override def toFragment: Fragment[Option[A]] = {
      val notEmpty: Fragment[Void] = BooleanOptionIsDefined(sqlField).toFragment
      val eq: Fragment[Option[A]]  = BooleanEq(sqlField, codec, Some(elt)).toFragment
      sql"($notEmpty AND $eq)"
    }
  }

  final case class BooleanOptionExists[A](sqlField: String, codec: Codec[Option[A]], elt: A)
}

class BooleanEqApplier extends Poly1 with MetaFieldBooleanSyntax {
  implicit def field[A]: Case.Aux[ExtractedField[A], IdBooleanAction[A]] = at { case (field, elt) => field === elt }
}

object booleanEqAndApplier extends BooleanEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= IdBooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF, OEF], IdBooleanAction[A]] = at { case (field, elt) =>
    BooleanAction.booleanEqAnd(field, elt)
  }
}

object booleanEqOrApplier extends BooleanEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= IdBooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF, OEF], IdBooleanAction[A]] = at { case (field, elt) =>
    BooleanAction.booleanEqOr(field, elt)
  }
}

class BooleanNEqApplier extends Poly1 with MetaFieldBooleanSyntax {
  implicit def field[A]: Case.Aux[ExtractedField[A], IdBooleanAction[A]] = at { case (field, elt) => field <> elt }
}

object booleanNEqAndApplier extends BooleanNEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= IdBooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF, OEF], IdBooleanAction[A]] = at { case (meta, elt) =>
    BooleanAction.booleanNEqAnd(meta, elt)
  }
}

object booleanNEqOrApplier extends BooleanNEqApplier {
  implicit def meta[A, F <: HList, MF <: HList, EF <: HList, OEF <: HList, MO <: HList, FO, CO](implicit
    mapper: Mapper.Aux[this.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanOrFolder.type, FO],
    ev: FO =:= IdBooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): Case.Aux[ExtractedMeta[A, F, MF, EF, OEF], IdBooleanAction[A]] = at { case (meta, elt) =>
    BooleanAction.booleanNEqOr(meta, elt)
  }
}

object actionBooleanAndFolder extends Poly2 {
  implicit def folder[A, B]: Case.Aux[IdBooleanAction[A], IdBooleanAction[B], IdBooleanAction[A ~ B]] =
    at(_ && _)
}

object actionBooleanOrFolder extends Poly2 {
  implicit def folder[A, B]: Case.Aux[IdBooleanAction[A], IdBooleanAction[B], IdBooleanAction[A ~ B]] =
    at(_ || _)
}
