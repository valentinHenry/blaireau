// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.actions.BooleanAction
import blaireau.metas.Meta
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import shapeless.HList
import skunk.data.Completion
import skunk.implicits.toStringOps
import skunk.util.Origin
import skunk.{Command, Session}

final class DeleteCommandBuilder[T, F <: HList, MF <: HList, EF <: HList, OEF <: HList, WC, W](
  tableName: String,
  private[blaireau] val meta: Meta.Aux[T, F, MF, EF, OEF],
  private[blaireau] val where: BooleanAction[WC, W]
) extends WhereOps[T, F, MF, EF, OEF, WC, W] {
  type SelfT[T0, F0 <: HList, MF0 <: HList, EF0 <: HList, OEF0 <: HList, WC0, W0] =
    DeleteCommandBuilder[T0, F0, MF0, EF0, OEF0, WC0, W0]

  override def withWhere[NWC, NW](newWhere: BooleanAction[NWC, NW]): DeleteCommandBuilder[T, F, MF, EF, OEF, NWC, NW] =
    new DeleteCommandBuilder[T, F, MF, EF, OEF, NWC, NW](tableName, meta, newWhere)

  def toCommand: Command[W] = {
    val deleteFragment = FragmentUtils.const(s"DELETE FROM $tableName")
    val whereFragment  = where.toFragment.contramap(where.to)
    sql"$deleteFragment WHERE $whereFragment".command
  }

  def commandIn: W = where.elt

  def execute[M[_]: MonadCancelThrow](s: Session[M])(implicit origin: Origin): M[Completion] =
    s.prepare(toCommand).use(_.execute(commandIn))
}
