// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.assignment.AssignmentAction
import blaireau.dsl.filtering.{BooleanAction, WhereBuilder}
import blaireau.metas.Meta
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import shapeless.HList
import skunk.data.Completion
import skunk.implicits.toStringOps
import skunk.{Command, Session, ~}

final class UpdateCommandBuilder[T, F <: HList, MF <: HList, EF <: HList, U, WC, W](
  tableName: String,
  private[blaireau] val meta: Meta.Aux[T, F, MF, EF],
  updatedFields: AssignmentAction[U],
  private[blaireau] val where: BooleanAction[WC, W]
) extends WhereBuilder[T, F, MF, EF, WC, W] {

  override type SelfT[T0, F0 <: HList, MF0 <: HList, EF0 <: HList, WC0, W0] =
    UpdateCommandBuilder[T0, F0, MF0, EF0, U, WC0, W0]

  override def withWhere[NWC, NW](
    newWhere: BooleanAction[NWC, NW]
  ): UpdateCommandBuilder[T, F, MF, EF, U, NWC, NW] =
    new UpdateCommandBuilder[T, F, MF, EF, U, NWC, NW](tableName, meta, updatedFields, newWhere)

  def toCommand: Command[U ~ W] = {
    val updateFragment     = FragmentUtils.const(s"UPDATE TABLE $tableName")
    val assignmentFragment = updatedFields.toFragment
    val whereFragment      = where.toFragment.contramap(where.to)
    sql"$updateFragment SET $assignmentFragment WHERE $whereFragment".command
  }

  def commandIn: U ~ W = (updatedFields.elt, where.elt)

  def execute[M[_]: MonadCancelThrow](s: Session[M]): M[Completion] =
    s.prepare(toCommand).use(_.execute(commandIn))
}
