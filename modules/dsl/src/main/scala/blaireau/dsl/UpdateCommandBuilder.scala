// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.dsl.actions.{AssignmentAction, BooleanAction, actionAssignmentFolder, assignmentMapper}
import blaireau.metas.Meta
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import shapeless.HList
import skunk.data.Completion
import skunk.implicits.toStringOps
import skunk.{Command, Session, ~}

class UpdateCommandBuilder[T, F <: HList, MF <: HList, EF <: HList, U, W](
  tableName: String,
  meta: Meta.Aux[T, F, MF, EF],
  updatedFields: AssignmentAction[U],
  where: BooleanAction[W]
) {

  def where[A](f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]): UpdateCommandBuilder[T, F, MF, EF, U, A] =
    new UpdateCommandBuilder(tableName, meta, updatedFields, f(meta))

  def whereAnd[A](f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]): UpdateCommandBuilder[T, F, MF, EF, U, (W, A)] =
    new UpdateCommandBuilder(tableName, meta, updatedFields, where && f(meta))

  def whereOr[A](f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]): UpdateCommandBuilder[T, F, MF, EF, U, (W, A)] =
    new UpdateCommandBuilder(tableName, meta, updatedFields, where || f(meta))

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
