// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.dsl.actions.{AssignmentAction, BooleanAction, actionAssignmentFolder, assignmentApplier}
import blaireau.metas.Meta
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import shapeless.HList
import skunk.data.Completion
import skunk.implicits.toStringOps
import skunk.{Command, Session, ~}

final class UpdateCommandBuilder[T, F <: HList, MF <: HList, EF <: HList, U, W](
  tableName: String,
  private[blaireau] val meta: Meta.Aux[T, F, MF, EF],
  updatedFields: AssignmentAction[U],
  private[blaireau] val where: BooleanAction[W]
) extends WhereOps[T, F, MF, EF, W] {

  override type SelfT[T0, F0 <: HList, MF0 <: HList, EF0 <: HList, W0] = UpdateCommandBuilder[T0, F0, MF0, EF0, U, W0]

  override def withWhere[NW](newWhere: BooleanAction[NW]): UpdateCommandBuilder[T, F, MF, EF, U, NW] =
    new UpdateCommandBuilder[T, F, MF, EF, U, NW](tableName, meta, updatedFields, newWhere)

  def toCommand: Command[U ~ W] = {
    val updateFragment     = FragmentUtils.const(s"UPDATE TABLE $tableName")
    val assignmentFragment = updatedFields.toFragment
    val whereFragment      = where.toFragment
    sql"$updateFragment SET $assignmentFragment WHERE $whereFragment".command
  }

  def commandIn: U ~ W = (updatedFields.elt, where.elt)

  def execute[M[_]: MonadCancelThrow](s: Session[M]): M[Completion] =
    s.prepare(toCommand).use(_.execute(commandIn))
}
