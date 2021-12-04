// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.metas.{ExportedMeta, Meta, MetaField}
import shapeless.ops.record.Selector
import shapeless.{::, HList, HNil, Witness}
import shapeless.record._

class Table[T, MT, MF <: HList](val name: String, meta: Meta.Aux[MT, MF]) {
  class FieldSelector[S <: HList](private[blaireau] val fields: S) {
    def get[A](select: MF => MetaField[A]): FieldSelector[MetaField[A] :: S] =
      new FieldSelector[MetaField[A] :: S](select(meta.metaFields) :: fields)
  }

  type SelectBuilder[S <: HList] = SelectQueryBuilder[T, MT, MF, S]
  private[this] def select[S <: HList](selects: S): SelectBuilder[S] =
    new SelectQueryBuilder[T, MT, MF, S](name, meta)(selects)

  def select: SelectBuilder[HNil] = select(HNil)
  def select[S <: HList](f: FieldSelector[HNil] => FieldSelector[S]): SelectBuilder[S] =
    select(f(new FieldSelector[HNil](HNil)).fields)

  // TODO find a way to make these using macros
  def select[F1](k1: Witness)(implicit
    s1: Selector.Aux[MF, k1.T, F1]
  ): SelectBuilder[F1 :: HNil] =
    select(meta.metaFields.get(k1) :: HNil)

  def select[F1, F2](k1: Witness, k2: Witness)(implicit
    s1: Selector.Aux[MF, k1.T, F1],
    s2: Selector.Aux[MF, k2.T, F2]
  ): SelectBuilder[F1 :: F2 :: HNil] =
    select(meta.metaFields.get(k1) :: meta.metaFields.get(k2) :: HNil)
}

object Table {
  def apply[T](name: String)(implicit m: ExportedMeta[T]): Table[T, m.MetaT, m.MetaF] =
    new Table[T, m.MetaT, m.MetaF](name, m.meta)
}
