// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import blaireau.metas.{Meta, MetaField}
import shapeless.ops.record.Selector
import shapeless.record._
import shapeless.tag.@@
import shapeless.{::, HList, HNil, Witness}

class Table[T, F <: HList, MF <: HList](name: String, val meta: Meta.Aux[T, F, MF]) {
  class FieldSelector[S <: HList](private[blaireau] val fields: S) {
    def get[A](select: F => MetaField[A]): FieldSelector[MetaField[A] :: S] =
      new FieldSelector[MetaField[A] :: S](select(meta.fields) :: fields)
  }

  type SelectQuery[S <: HList] = SelectQueryBuilder[T, F, MF, S, skunk.Void]
  private[this] def select[S <: HList](selects: S): SelectQuery[S] =
    new SelectQueryBuilder[T, F, MF, S, skunk.Void](name, meta, selects, where = Action.BooleanOp.empty)

  def select: SelectQuery[MF] = select(meta.metaFields)
  def select[S <: HList](f: FieldSelector[HNil] => FieldSelector[S]): SelectQuery[S] =
    select(f(new FieldSelector[HNil](HNil)).fields)

  // TODO find a way to make these using macros
  def select[F1](k1: Witness)(implicit
    s1: Selector.Aux[F, k1.T, F1]
  ): SelectQuery[F1 :: HNil] =
    select(meta.fields.get(k1) :: HNil)

  def select[F1, F2](k1: String, k2: String)(implicit
    s1: Selector.Aux[F, Symbol @@ k1.type, F1],
    s2: Selector.Aux[F, Symbol @@ k2.type, F2]
  ): SelectQuery[F1 :: F2 :: HNil] =
    select(meta.fields.record.selectDynamic(k1) :: meta.fields.record.selectDynamic(k2) :: HNil)

  def select[F1, F2, F3](k1: String, k2: String, k3: String)(implicit
    s1: Selector.Aux[F, Symbol @@ k1.type, F1],
    s2: Selector.Aux[F, Symbol @@ k2.type, F2],
    s3: Selector.Aux[F, Symbol @@ k3.type, F3]
  ): SelectQuery[F1 :: F2 :: F3 :: HNil] =
    select(
      meta.fields.record.selectDynamic(k1) ::
        meta.fields.record.selectDynamic(k2) ::
        meta.fields.record.selectDynamic(k3) ::
        HNil
    )
}

object Table {
  def apply[T](name: String)(implicit m: Meta[T]): Table[T, m.F, m.MF] = new Table[T, m.F, m.MF](name, m)
}
