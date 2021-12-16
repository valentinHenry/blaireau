// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.dsl.actions.{AssignmentAction, BooleanAction, assignmentMapper, fieldAssignmentFolder}
import blaireau.metas.{FieldProduct, Meta, MetaElt, MetaField}
import shapeless.HList
import shapeless.ops.hlist.{LeftReducer, Mapper, ToList}
import skunk.Codec

class Table[T, F <: HList, MF <: HList, EF <: HList](name: String, val meta: Meta.Aux[T, F, MF, EF]) {
  type SelectQuery[S <: HList, SC] = SelectQueryBuilder[T, F, MF, EF, SC, skunk.Void]
  private[this] def select[S <: HList, SC](selects: S, selectCodec: Codec[SC])(implicit
    toList: ToList[S, MetaField[_]]
  ): SelectQuery[S, SC] =
    new SelectQueryBuilder[T, F, MF, EF, SC, skunk.Void](
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

  type UpdateCommand[U] = UpdateQueryBuilder[T, F, MF, EF, U, skunk.Void]
  private[this] def update[U](updates: AssignmentAction[U]): UpdateCommand[U] =
    new UpdateQueryBuilder[T, F, MF, EF, U, skunk.Void](name, meta, updates, BooleanAction.empty)

  def update[MEF <: HList, LRO, UF](elt: T)(implicit
    m: Mapper.Aux[assignmentMapper.type, EF, MEF],
    r: LeftReducer.Aux[MEF, fieldAssignmentFolder.type, LRO],
    ev: LRO =:= AssignmentAction[UF]
  ): UpdateCommand[UF] =
    update(meta.extract(elt).map(assignmentMapper).reduceLeft(fieldAssignmentFolder))

  def update[U](u: MetaElt.Aux[T, F, MF] => AssignmentAction[U]): UpdateCommand[U] =
    update(u(meta))
}

object Table {
  def apply[T](name: String)(implicit m: Meta[T]): Table[T, m.F, m.MF, m.EF] = new Table[T, m.F, m.MF, m.EF](name, m)
}
