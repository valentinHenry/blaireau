// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.dsl.utils.FragmentUtils
import blaireau.metas.{Meta, MetaField, TableSchema}
import shapeless.labelled.{FieldType, KeyTag}
import shapeless.ops.hlist.{Mapper, Reverse, RightReducer, ToList}
import shapeless.{HList, Poly1, Poly2}
import skunk.implicits.toStringOps
import skunk.util.Twiddler
import skunk.{Codec, Query}
import skunk.~

object metaFieldToCodec extends Poly1 {
  implicit def metaFieldCase[F]: Case.Aux[MetaField[F], Codec[F]]               = at(_.codec)
  implicit def metaKeyTag[K, F]: Case.Aux[FieldType[K, MetaField[F]], Codec[F]] = at(_.codec)
}

object codecReducer extends Poly2 {
  implicit def codecFolder[A, B]: Case.Aux[Codec[A], Codec[B], Codec[B ~ A]] = at((l, r) => r ~ l)
}

class SelectQueryBuilder[T, MT, MF <: HList, S <: HList, W](
  tableName: String,
  meta: Meta.Aux[MT, MF],
  select: S,
  where: Action.BooleanOp[W]
) {

  def where[A](f: TableSchema.Aux[MT, MF] => Action.BooleanOp[A]) =
    new SelectQueryBuilder[T, MT, MF, S, A](tableName, meta, select, f(meta))

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
