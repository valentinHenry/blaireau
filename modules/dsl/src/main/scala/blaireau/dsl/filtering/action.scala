// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.dsl.actions.{Action, FieldNamePicker, IMapper}
import blaireau.metas.{Meta, MetaField, MetaUtils}
import blaireau.utils.FragmentUtils
import shapeless.HList
import shapeless.ops.hlist.{LeftReducer, Mapper}
import skunk.implicits.{toIdOps, toStringOps}
import skunk.util.Twiddler
import skunk.{Codec, Fragment, Void, ~}

sealed trait BooleanAction[A] extends Action[A] with Product with Serializable {
  self =>
  def &&[B](right: BooleanAction[B]): BooleanAction[A ~ B] =
    ForgedBoolean(
      self.codec ~ right.codec,
      self.elt ~ right.elt,
      picker => sql"(${self.toFragment(picker)} AND ${right.toFragment(picker)})"
    )

  def ||[B](right: BooleanAction[B]): BooleanAction[A ~ B] =
    ForgedBoolean(
      self.codec ~ right.codec,
      self.elt ~ right.elt,
      picker => sql"(${self.toFragment(picker)} OR ${right.toFragment(picker)})"
    )

  def unary_! : BooleanAction[A] = ForgedBoolean(
    self.codec,
    self.elt,
    picker => sql"(NOT ${self.toFragment(picker)})"
  )
}

private[blaireau] final case class ForgedBoolean[A](
  codec: Codec[A],
  elt: A,
  fragment: FieldNamePicker => Fragment[A]
) extends BooleanAction[A] {
  override def toFragment(picker: FieldNamePicker): Fragment[A] = fragment(picker)
}

object BooleanAction {
  implicit def imapper: IMapper[BooleanAction] = new IMapper[BooleanAction] {
    override def imap[A, B](m: BooleanAction[A])(f: A => B)(g: B => A): BooleanAction[B] =
      BooleanAction.imap(m)(f)(g)
  }

  def empty: BooleanAction[Void] = ForgedBoolean(
    Void.codec,
    Void,
    _ => sql"TRUE"
  )

  def imap[A, B](
    m: BooleanAction[A]
  )(f: A => B)(g: B => A): BooleanAction[B] =
    ForgedBoolean(
      m.codec.imap(f)(g),
      f(m.elt),
      picker => m.toFragment(picker).contramap(g)
    )

  private[blaireau] def booleanEqAnd[A, F <: HList, MF <: HList, EF <: HList, MO <: HList, FO, CO, P <: HList](
    meta: Meta.Aux[A, F, MF, EF],
    elt: A
  )(implicit
    mapper: Mapper.Aux[booleanEqAndApplier.type, EF, MO],
    r: LeftReducer.Aux[MO, actionBooleanAndFolder.type, FO],
    ev: FO =:= BooleanAction[CO],
    tw: Twiddler.Aux[A, CO]
  ): BooleanAction[A] =
    MetaUtils.applyExtract[booleanEqAndApplier.type, actionBooleanAndFolder.type, A, EF, MO, FO, CO, BooleanAction](
      elt,
      meta.extract
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
    MetaUtils.applyExtract[booleanEqOrApplier.type, actionBooleanOrFolder.type, A, EF, MO, FO, CO, BooleanAction](
      elt,
      meta.extract
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
    MetaUtils.applyExtract[booleanNEqAndApplier.type, actionBooleanAndFolder.type, A, EF, MO, FO, CO, BooleanAction](
      elt,
      meta.extract
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
    MetaUtils.applyExtract[booleanNEqOrApplier.type, actionBooleanOrFolder.type, A, EF, MO, FO, CO, BooleanAction](
      elt,
      meta.extract
    )

  trait VoidBooleanAction extends BooleanAction[Void] {
    final override val codec: Codec[Void] = Void.codec

    final override def elt: Void = Void
  }

  final case class BooleanEq[A](field: MetaField[A], elt: A) extends Action.Op[A]("=", field) with BooleanAction[A]

  final case class BooleanLike[A](field: MetaField[A], elt: A) extends Action.Op[A]("like", field) with BooleanAction[A]

  final case class BooleanNEq[A](field: MetaField[A], elt: A) extends Action.Op[A]("<>", field) with BooleanAction[A]

  final case class BooleanGt[A](field: MetaField[A], elt: A) extends Action.Op[A](">", field) with BooleanAction[A]

  final case class BooleanGtEq[A](field: MetaField[A], elt: A) extends Action.Op[A](">=", field) with BooleanAction[A]

  final case class BooleanLt[A](field: MetaField[A], elt: A) extends Action.Op[A]("<", field) with BooleanAction[A]

  final case class BooleanLtEq[A](field: MetaField[A], elt: A) extends Action.Op[A]("<=", field) with BooleanAction[A]

  final case class BooleanOptionIsEmpty(field: MetaField[_]) extends VoidBooleanAction {
    override def toFragment(picker: FieldNamePicker): Fragment[Void] =
      FragmentUtils.const(s"${picker.get(field)} IS NULL")
  }

  final case class BooleanOptionIsDefined(field: MetaField[_]) extends VoidBooleanAction {
    override def toFragment(picker: FieldNamePicker): Fragment[Void] =
      FragmentUtils.const(s"${picker.get(field)} IS NOT NULL")
  }

  final case class BooleanOptionExists[A](sqlField: String, codec: Codec[Option[A]], elt: A)
}
