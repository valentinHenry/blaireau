// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.dsl.actions.{Action, IMapper}
import blaireau.metas.{Meta, MetaUtils}
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
      sql"(${self.toFragment} AND ${right.toFragment})"
    )

  def ||[B](right: BooleanAction[B]): BooleanAction[A ~ B] =
    ForgedBoolean(
      self.codec ~ right.codec,
      self.elt ~ right.elt,
      sql"(${self.toFragment} OR ${right.toFragment})"
    )

  def unary_! : BooleanAction[A] = ForgedBoolean(
    self.codec,
    self.elt,
    sql"(NOT ${self.toFragment})"
  )
}

private[blaireau] final case class ForgedBoolean[A](
  codec: Codec[A],
  elt: A,
  fragment: Fragment[A]
) extends BooleanAction[A] {
  override def toFragment: Fragment[A] = fragment
}

object BooleanAction {
  implicit def imapper: IMapper[BooleanAction] = new IMapper[BooleanAction] {
    override def imap[A, B](m: BooleanAction[A])(f: A => B)(g: B => A): BooleanAction[B] =
      BooleanAction.imap(m)(f)(g)
  }

  def empty: BooleanAction[Void] = ForgedBoolean(
    Void.codec,
    Void,
    sql"TRUE"
  )

  def imap[A, B](
    m: BooleanAction[A]
  )(f: A => B)(g: B => A): BooleanAction[B] =
    ForgedBoolean(
      m.codec.imap(f)(g),
      f(m.elt),
      m.toFragment.contramap(g)
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
    extends Action.Op[A]("<=", sqlField)
    with BooleanAction[A]

  final case class BooleanOptionIsEmpty(sqlField: String) extends VoidBooleanAction {
    override def toFragment: Fragment[Void] = FragmentUtils.const(s"$sqlField IS NULL")
  }

  final case class BooleanOptionIsDefined(sqlField: String) extends VoidBooleanAction {
    override def toFragment: Fragment[Void] = FragmentUtils.const(s"$sqlField IS NOT NULL")
  }

  final case class BooleanOptionExists[A](sqlField: String, codec: Codec[Option[A]], elt: A)
}
