// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import blaireau.metas.{Meta, MetaElt, MetaField}
import blaireau.utils.FragmentUtils
import shapeless.labelled.FieldType
import shapeless.ops.hlist.{Mapper, Reverse, RightReducer, ToList}
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

class SelectQueryBuilder[T, F <: HList, MF <: HList, S <: HList, W](
  tableName: String,
  meta: Meta.Aux[T, F, MF],
  select: S,
  where: Action.BooleanOp[W]
) {

  def where[A](f: MetaElt.Aux[T, F, MF] => Action.BooleanOp[A]) =
    new SelectQueryBuilder[T, F, MF, S, A](tableName, meta, select, f(meta))

  def whereAnd[A](f: MetaElt.Aux[T, F, MF] => Action.BooleanOp[A]) =
    new SelectQueryBuilder[T, F, MF, S, (W, A)](tableName, meta, select, where && f(meta))

  def whereOr[A](f: MetaElt.Aux[T, F, MF] => Action.BooleanOp[A]) =
    new SelectQueryBuilder[T, F, MF, S, (W, A)](tableName, meta, select, where || f(meta))

  def toInstanceQuery(implicit
    toList: ToList[S, MetaField[_]],
    ev: S =:= MF
  ): Query[W, T] = {
    val untypedFields  = select.toList[MetaField[_]]
    val fieldsToSelect = untypedFields.map(_.sqlName).mkString(",")

    val selectFromFragment = FragmentUtils.const(s"SELECT $fieldsToSelect FROM $tableName")

    val filter = where.toFragment

    sql"$selectFromFragment WHERE $filter".query[T](meta.codec)
  }

  def toQuery[RS <: HList, MO <: HList, RO, TO](implicit
    toList: ToList[S, MetaField[_]],
    reverseS: Reverse.Aux[S, RS],
    mapperEv: Mapper.Aux[metaFieldToCodec.type, RS, MO],
    reducerEv: RightReducer.Aux[MO, codecReducer.type, Codec[RO]]
  ): Query[W, RO] = {
    val untypedFields  = select.toList[MetaField[_]]
    val fieldsToSelect = untypedFields.map(_.sqlName).mkString(",")

    val selectFromFragment = FragmentUtils.const(s"SELECT $fieldsToSelect FROM $tableName")

    val filter = where.toFragment

    val selectCodecs = select.reverse.map(metaFieldToCodec).reduceRight(codecReducer)

    sql"$selectFromFragment WHERE $filter".query[RO](selectCodecs)
  }

  def queryIn: W = where.elt

}
