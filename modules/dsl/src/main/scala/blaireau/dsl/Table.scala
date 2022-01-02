// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.dsl.actions.FieldNamePicker
import blaireau.dsl.assignment.{AssignmentAction, actionAssignmentFolder, assignmentApplier}
import blaireau.dsl.builders.{DeleteCommandBuilder, InsertCommandBuilder, SelectQueryBuilder, UpdateCommandBuilder}
import blaireau.dsl.filtering.BooleanAction
import blaireau.metas.{FieldProduct, Meta, MetaField}
import shapeless.HList
import shapeless.ops.hlist.{LeftReducer, Mapper, ToList}
import skunk.Codec
import skunk.util.Twiddler

import scala.util.chaining.scalaUtilChainingOps

final class Table[T, F <: HList, MF <: HList, EF <: HList](
  tableName: String,
  picker: FieldNamePicker,
  val meta: Meta.Aux[T, F, MF, EF]
) {
  type SelectQuery[S <: HList, SC] = SelectQueryBuilder[T, F, MF, EF, SC, skunk.Void]
  type UpdateCommand[U]            = UpdateCommandBuilder[T, F, MF, EF, U, skunk.Void]

  def columns(overrides: (Meta.Aux[T, F, MF, EF] => (MetaField[_], String))*): Table[T, F, MF, EF] = {
    val fieldPicker = overrides
      .map(_(meta))
      .map { case (mf, name) => mf.id -> name }
      .toMap
      .pipe(new FieldNamePicker(_, meta.idMapping))

    new Table[T, F, MF, EF](
      tableName,
      fieldPicker,
      meta
    )
  }

  def select(implicit
    toList: ToList[MF, MetaField[_]]
  ): SelectQuery[MF, T] = select(meta.metaFields, meta.codec)

  def select[SMF <: HList, SC](s: Meta.Aux[T, F, MF, EF] => FieldProduct.Aux[SC, SMF])(implicit
    toList: ToList[SMF, MetaField[_]]
  ): SelectQuery[SMF, SC] = {
    val selected = s(meta)
    select(selected.metaFields, selected.codec)
  }

  private[this] def select[SMF <: HList, SC](selects: SMF, selectCodec: Codec[SC])(implicit
    toList: ToList[SMF, MetaField[_]]
  ): SelectQuery[SMF, SC] =
    new SelectQuery[SMF, SC](
      tableName,
      picker,
      meta,
      selects.toList[MetaField[_]].map(picker.get),
      selectCodec,
      where = BooleanAction.empty
    )

  def update[U](u: Meta.Aux[T, F, MF, EF] => AssignmentAction[U]): UpdateCommand[U] =
    update(u(meta))

  def update[MEF <: HList, LRO, UF](elt: T)(implicit
    m: Mapper.Aux[assignmentApplier.type, EF, MEF],
    r: LeftReducer.Aux[MEF, actionAssignmentFolder.type, LRO],
    ev: LRO =:= AssignmentAction[UF],
    tw: Twiddler.Aux[T, UF]
  ): UpdateCommand[T] =
    update(meta := elt)

  private[this] def update[U](updates: AssignmentAction[U]): UpdateCommand[U] =
    new UpdateCommand[U](tableName, picker, meta, updates, BooleanAction.empty)

  def delete: DeleteCommandBuilder[T, F, MF, EF, skunk.Void] =
    new DeleteCommandBuilder[T, F, MF, EF, skunk.Void](tableName, picker, meta, BooleanAction.empty)

  type InsertCommand[I] = InsertCommandBuilder[I, InsertCommandBuilder.Ev.Empty]

  def insert[IMF <: HList, IC](u: Meta.Aux[T, F, MF, EF] => FieldProduct.Aux[IC, IMF])(implicit
    toList: ToList[IMF, MetaField[_]]
  ): InsertCommand[IC] = {
    val inserted = u(meta)
    insert(inserted.metaFields, inserted.codec)
  }

  def insert(implicit toList: ToList[MF, MetaField[_]]): InsertCommand[T] =
    insert[MF, T](meta.metaFields, meta.codec)

  private[this] def insert[IMF <: HList, IC](insert: IMF, insertCodec: Codec[IC])(implicit
    toList: ToList[IMF, MetaField[_]]
  ): InsertCommand[IC] =
    InsertCommandBuilder.make(
      tableName,
      insert.toList[MetaField[_]].map(picker.get),
      insertCodec.asEncoder
    )
}

object Table {
  def apply[T](name: String)(implicit m: Meta[T]): Table[T, m.F, m.MF, m.EF] =
    new Table[T, m.F, m.MF, m.EF](name, new FieldNamePicker(Map.empty, m.idMapping), m)
}
