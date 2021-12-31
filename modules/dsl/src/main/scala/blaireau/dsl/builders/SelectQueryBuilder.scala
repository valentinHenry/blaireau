// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.filtering.{BooleanAction, WhereBuilder}
import blaireau.metas.Meta
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import cats.effect.kernel.Resource
import cats.syntax.applicative._
import shapeless.HList
import skunk.implicits.toStringOps
import skunk.util.Origin
import skunk.{Codec, Cursor, Query, Session}

final class SelectQueryBuilder[T, F <: HList, MF <: HList, EF <: HList, SC, WC, W](
  tableName: String,
  private[blaireau] val meta: Meta.Aux[T, F, MF, EF],
  select: List[String],
  selectCodec: Codec[SC],
  private[blaireau] val where: BooleanAction[WC, W]
) extends WhereBuilder[T, F, MF, EF, WC, W] {

  override type SelfT[T0, F0 <: HList, MF0 <: HList, EF0 <: HList, WC0, W0] =
    SelectQueryBuilder[T0, F0, MF0, EF0, SC, WC0, W0]

  override def withWhere[NWC, NW](
    newWhere: BooleanAction[NWC, NW]
  ): SelectQueryBuilder[T, F, MF, EF, SC, NWC, NW] =
    new SelectQueryBuilder[T, F, MF, EF, SC, NWC, NW](tableName, meta, select, selectCodec, newWhere)

  def toQuery: Query[W, SC] = {
    val fieldsToSelect = select.mkString(",")

    val selectFromFragment = FragmentUtils.const(s"SELECT $fieldsToSelect FROM $tableName")

    val filter = where.toFragment.contramap(where.to)

    sql"$selectFromFragment WHERE $filter".query[SC](selectCodec.asDecoder)
  }

  def queryIn: W = where.elt

  def option[M[_]: MonadCancelThrow](s: Session[M])(implicit origin: Origin): M[Option[SC]] =
    s.prepare(toQuery).use(_.option(queryIn))

  def unique[M[_]: MonadCancelThrow](s: Session[M])(implicit origin: Origin): M[SC] =
    s.prepare(toQuery).use(_.unique(queryIn))

  def cursor[M[_]](s: Session[M])(implicit origin: Origin): Resource[M, Cursor[M, SC]] =
    s.prepare(toQuery).flatMap(_.cursor(queryIn))

  def stream[M[_]: MonadCancelThrow](chunkSize: Int, s: Session[M])(implicit origin: Origin): M[fs2.Stream[M, SC]] =
    s.prepare(toQuery).use(_.stream(queryIn, chunkSize).pure[M])
}
