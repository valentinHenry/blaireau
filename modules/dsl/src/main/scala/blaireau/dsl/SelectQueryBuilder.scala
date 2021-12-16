// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.dsl.actions.BooleanAction
import blaireau.metas.{Meta, MetaElt}
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import cats.effect.kernel.Resource
import cats.syntax.applicative._
import shapeless.HList
import skunk.implicits.toStringOps
import skunk.{Codec, Cursor, Query, Session}

class SelectQueryBuilder[T, F <: HList, MF <: HList, EF <: HList, SC, W](
  tableName: String,
  meta: Meta.Aux[T, F, MF, EF],
  select: List[String],
  selectCodec: Codec[SC],
  where: BooleanAction[W]
) {

  def where[A](f: MetaElt.Aux[T, F, MF] => BooleanAction[A]): SelectQueryBuilder[T, F, MF, EF, SC, A] =
    new SelectQueryBuilder[T, F, MF, EF, SC, A](tableName, meta, select, selectCodec, f(meta))

  def whereAnd[A](f: MetaElt.Aux[T, F, MF] => BooleanAction[A]): SelectQueryBuilder[T, F, MF, EF, SC, (W, A)] =
    new SelectQueryBuilder[T, F, MF, EF, SC, (W, A)](tableName, meta, select, selectCodec, where && f(meta))

  def whereOr[A](f: MetaElt.Aux[T, F, MF] => BooleanAction[A]): SelectQueryBuilder[T, F, MF, EF, SC, (W, A)] =
    new SelectQueryBuilder[T, F, MF, EF, SC, (W, A)](tableName, meta, select, selectCodec, where || f(meta))

  def toQuery: Query[W, SC] = {
    val fieldsToSelect = select.mkString(",")

    val selectFromFragment = FragmentUtils.const(s"SELECT $fieldsToSelect FROM $tableName")

    val filter = where.toFragment

    sql"$selectFromFragment WHERE $filter".query[SC](selectCodec)
  }

  def queryIn: W = where.elt

  def option[M[_]: MonadCancelThrow](s: Session[M]): M[Option[SC]] =
    s.prepare(toQuery).use(_.option(queryIn))

  def unique[M[_]: MonadCancelThrow](s: Session[M]): M[SC] =
    s.prepare(toQuery).use(_.unique(queryIn))

  def cursor[M[_]](s: Session[M]): Resource[M, Cursor[M, SC]] =
    s.prepare(toQuery).flatMap(_.cursor(queryIn))

  def stream[M[_]: MonadCancelThrow](chunkSize: Int, s: Session[M]): M[fs2.Stream[M, SC]] =
    s.prepare(toQuery).use(_.stream(queryIn, chunkSize).pure[M])
}
