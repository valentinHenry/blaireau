// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.actions.FieldNamePicker
import blaireau.dsl.filtering.{BooleanAction, WhereBuilder}
import blaireau.metas.Meta
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import shapeless.HList
import skunk.data.Completion
import skunk.implicits.toStringOps
import skunk.util.Origin
import skunk.{Command, Session}

final class DeleteCommandBuilder[T, F <: HList, MF <: HList, EF <: HList, W](
  tableName: String,
  picker: FieldNamePicker,
  private[blaireau] val meta: Meta.Aux[T, F, MF, EF],
  private[blaireau] val where: BooleanAction[W]
) extends WhereBuilder[T, F, MF, EF, W] {
  type SelfT[W0] =
    DeleteCommandBuilder[T, F, MF, EF, W0]

  override def withWhere[NW](newWhere: BooleanAction[NW]): DeleteCommandBuilder[T, F, MF, EF, NW] =
    new DeleteCommandBuilder[T, F, MF, EF, NW](tableName, picker, meta, newWhere)

  def toCommand: Command[W] = {
    val deleteFragment = FragmentUtils.const(s"DELETE FROM $tableName")
    val whereFragment  = where.toFragment(picker)
    sql"$deleteFragment WHERE $whereFragment".command
  }

  def commandIn: W = where.elt

  def execute[M[_]: MonadCancelThrow](s: Session[M])(implicit origin: Origin): M[Completion] =
    s.prepare(toCommand).use(_.execute(commandIn))
}
