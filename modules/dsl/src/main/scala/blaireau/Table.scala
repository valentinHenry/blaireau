// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import blaireau.metas.{FieldProduct, Meta, MetaElt}
import shapeless.HList

class Table[T, F <: HList, MF <: HList](name: String, val meta: Meta.Aux[T, F, MF]) {
  type SelectQuery[S <: HList] = SelectQueryBuilder[T, F, MF, S, skunk.Void]
  private[this] def select[S <: HList](selects: S): SelectQuery[S] =
    new SelectQueryBuilder[T, F, MF, S, skunk.Void](name, meta, selects, where = Action.BooleanOp.empty)

  def select: SelectQuery[MF] = select(meta.metaFields)
  def select[O <: HList](s: MetaElt.Aux[T, F, MF] => FieldProduct.Aux[O]): SelectQuery[O] =
    select(s(meta).metaFields)
}

object Table {
  def apply[T](name: String)(implicit m: Meta[T]): Table[T, m.F, m.MF] = new Table[T, m.F, m.MF](name, m)
}
