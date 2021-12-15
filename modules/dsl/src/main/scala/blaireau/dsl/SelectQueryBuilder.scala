// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.metas.{Meta, MetaElt, MetaField}
import blaireau.utils.FragmentUtils
import shapeless.labelled.FieldType
import shapeless.ops.hlist.ToList
import shapeless.{HList, Poly1, Poly2}
import skunk.implicits.toStringOps
import skunk.{Codec, Query, ~}

object metaFieldToCodec extends Poly1 {
  implicit def metaFieldCase[F]: Case.Aux[MetaField[F], Codec[F]]               = at(_.codec)
  implicit def metaKeyTag[K, F]: Case.Aux[FieldType[K, MetaField[F]], Codec[F]] = at(_.codec)
}

object codecReducer extends Poly2 {
  implicit def codecFolder[A, B]: Case.Aux[Codec[A], Codec[B], Codec[B ~ A]] = at((l, r) => r ~ l)
}

class SelectQueryBuilder[T, F <: HList, MF <: HList, S <: HList, SC, W](
  tableName: String,
  meta: Meta.Aux[T, F, MF],
  select: S,
  selectCodec: Codec[SC],
  where: Action.BooleanOp[W]
) {

  def where[A](f: MetaElt.Aux[T, F, MF] => Action.BooleanOp[A]) =
    new SelectQueryBuilder[T, F, MF, S, SC, A](tableName, meta, select, selectCodec, f(meta))

  def whereAnd[A](f: MetaElt.Aux[T, F, MF] => Action.BooleanOp[A]) =
    new SelectQueryBuilder[T, F, MF, S, SC, (W, A)](tableName, meta, select, selectCodec, where && f(meta))

  def whereOr[A](f: MetaElt.Aux[T, F, MF] => Action.BooleanOp[A]) =
    new SelectQueryBuilder[T, F, MF, S, SC, (W, A)](tableName, meta, select, selectCodec, where || f(meta))

  def toQuery[RS <: HList, MO <: HList, TO](implicit
    toList: ToList[S, MetaField[_]]
  ): Query[W, SC] = {
    val untypedFields  = select.toList[MetaField[_]]
    val fieldsToSelect = untypedFields.map(_.sqlName).mkString(",")

    val selectFromFragment = FragmentUtils.const(s"SELECT $fieldsToSelect FROM $tableName")

    val filter = where.toFragment

    sql"$selectFromFragment WHERE $filter".query[SC](selectCodec)
  }

  def queryIn: W = where.elt

}
