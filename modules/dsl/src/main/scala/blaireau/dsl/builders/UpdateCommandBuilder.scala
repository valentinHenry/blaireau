// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.actions.FieldNamePicker
import blaireau.dsl.assignment.AssignmentAction
import blaireau.dsl.filtering.{BooleanAction, WhereBuilder}
import blaireau.metas.Meta
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import shapeless.HList
import skunk.data.Completion
import skunk.implicits.toStringOps
import skunk.{Command, Session, ~}

final class UpdateCommandBuilder[T, F <: HList, MF <: HList, EF <: HList, U, W](
  tableName: String,
  picker: FieldNamePicker,
  private[blaireau] val meta: Meta.Aux[T, F, MF, EF],
  updatedFields: AssignmentAction[U],
  private[blaireau] val where: BooleanAction[W]
) extends WhereBuilder[T, F, MF, EF, W] {

  override type SelfT[W0] = UpdateCommandBuilder[T, F, MF, EF, U, W0]

  override def withWhere[NW](
    newWhere: BooleanAction[NW]
  ): UpdateCommandBuilder[T, F, MF, EF, U, NW] =
    new UpdateCommandBuilder[T, F, MF, EF, U, NW](tableName, picker, meta, updatedFields, newWhere)

  def toCommand: Command[U ~ W] = {
    val updateFragment     = FragmentUtils.const(s"UPDATE $tableName")
    val assignmentFragment = updatedFields.toFragment(picker)
    val whereFragment      = where.toFragment(picker)
    sql"$updateFragment SET $assignmentFragment WHERE $whereFragment".command
  }

  def commandIn: U ~ W = (updatedFields.elt, where.elt)

  def execute[M[_]: MonadCancelThrow](s: Session[M]): M[Completion] =
    s.prepare(toCommand).use(_.execute(commandIn))
}
