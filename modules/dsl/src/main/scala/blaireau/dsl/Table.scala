// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.dsl.actions.{AssignmentAction, BooleanAction}
import blaireau.metas.{FieldProduct, Meta, MetaElt, MetaField}
import shapeless.HList
import shapeless.ops.hlist.ToList
import skunk.Codec

class Table[T, F <: HList, MF <: HList](name: String, val meta: Meta.Aux[T, F, MF]) {
  type SelectQuery[S <: HList, SC] = SelectQueryBuilder[T, F, MF, SC, skunk.Void]
  private[this] def select[S <: HList, SC](selects: S, selectCodec: Codec[SC])(implicit
    toList: ToList[S, MetaField[_]]
  ): SelectQuery[S, SC] =
    new SelectQueryBuilder[T, F, MF, SC, skunk.Void](
      name,
      meta,
      selects.toList[MetaField[_]].map(_.sqlName),
      selectCodec,
      where = BooleanAction.empty
    )

  def select(implicit
    toList: ToList[MF, MetaField[_]]
  ): SelectQuery[MF, T] = select(meta.metaFields, meta.codec)

  def select[MFO <: HList, OC](s: MetaElt.Aux[T, F, MF] => FieldProduct.Aux[OC, MFO])(implicit
    toList: ToList[MFO, MetaField[_]]
  ): SelectQuery[MFO, OC] = {
    val selected = s(meta)
    select(selected.metaFields, selected.codec)
  }

  type UpdateCommand[U] = UpdateQueryBuilder[T, F, MF, U, skunk.Void]
  private[this] def update[U](updates: AssignmentAction[U]): UpdateCommand[U] =
    new UpdateQueryBuilder[T, F, MF, U, skunk.Void](name, meta, updates, BooleanAction.empty)

  def update(elt: T): UpdateCommand[T] = ??? // TODO find a way to unwrap T into a list of Field -> T for assignment

  def update[U](u: MetaElt.Aux[T, F, MF] => AssignmentAction[U]): UpdateCommand[U] =
    update(u(meta))
}

object Table {
  def apply[T](name: String)(implicit m: Meta[T]): Table[T, m.F, m.MF] = new Table[T, m.F, m.MF](name, m)
}
