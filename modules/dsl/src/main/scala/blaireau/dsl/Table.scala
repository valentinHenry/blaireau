// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.metas.{ExportedMeta, Meta, MetaField}
import shapeless.ops.record.Selector
import shapeless.{::, HList, HNil, Witness}
import shapeless.record._
import shapeless.tag.@@

class Table[T, MT, MF <: HList](val name: String, meta: Meta.Aux[MT, MF]) {
  class FieldSelector[S <: HList](private[blaireau] val fields: S) {
    def get[A](select: MF => MetaField[A]): FieldSelector[MetaField[A] :: S] =
      new FieldSelector[MetaField[A] :: S](select(meta.metaFields) :: fields)
  }

  type SelectQuery[S <: HList] = SelectQueryBuilder[T, MT, MF, S, skunk.Void]
  private[this] def select[S <: HList](selects: S): SelectQuery[S] =
    new SelectQueryBuilder[T, MT, MF, S, skunk.Void](name, meta, selects, where = Action.BooleanOp.empty)

  def select: SelectQuery[MF] = select(meta.metaFields)
  def select[S <: HList](f: FieldSelector[HNil] => FieldSelector[S]): SelectQuery[S] =
    select(f(new FieldSelector[HNil](HNil)).fields)

  // TODO find a way to make these using macros
  def select[F1](k1: Witness)(implicit
    s1: Selector.Aux[MF, k1.T, F1]
  ): SelectQuery[F1 :: HNil] =
    select(meta.metaFields.get(k1) :: HNil)

  def select[F1, F2](k1: String, k2: String)(implicit
    s1: Selector.Aux[MF, Symbol @@ k1.type, F1],
    s2: Selector.Aux[MF, Symbol @@ k2.type, F2]
  ): SelectQuery[F1 :: F2 :: HNil] =
    select(meta.metaFields.record.selectDynamic(k1) :: meta.metaFields.record.selectDynamic(k2) :: HNil)

  def select[F1, F2, F3](k1: String, k2: String, k3: String)(implicit
    s1: Selector.Aux[MF, Symbol @@ k1.type, F1],
    s2: Selector.Aux[MF, Symbol @@ k2.type, F2],
    s3: Selector.Aux[MF, Symbol @@ k3.type, F3]
  ): SelectQuery[F1 :: F2 :: F3 :: HNil] =
    select(
      meta.metaFields.record.selectDynamic(k1) ::
        meta.metaFields.record.selectDynamic(k2) ::
        meta.metaFields.record.selectDynamic(k3) ::
        HNil
    )
}

object Table {
  def apply[T](name: String)(implicit m: ExportedMeta[T]): Table[T, m.MetaT, m.MetaF] =
    new Table[T, m.MetaT, m.MetaF](name, m.meta)
}
