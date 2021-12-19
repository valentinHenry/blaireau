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

final class DeleteCommandBuilder[T, F <: HList, MF <: HList, EF <: HList, W](
  tableName: String,
  private[blaireau] val meta: Meta.Aux[T, F, MF, EF],
  private[blaireau] val where: BooleanAction[W]
) extends WhereOps[T, F, MF, EF, W] {
  type SelfT[T0, F0 <: HList, MF0 <: HList, EF0 <: HList, W0] = DeleteCommandBuilder[T0, F0, MF0, EF0, W0]

  override def withWhere[NW](newWhere: BooleanAction[NW]): DeleteCommandBuilder[T, F, MF, EF, NW] =
    new DeleteCommandBuilder[T, F, MF, EF, NW](tableName, meta, newWhere)

  def toCommand: Command[W] = {
    val deleteFragment = FragmentUtils.const(s"DELETE FROM $tableName")
    val whereFragment  = where.toFragment
    sql"$deleteFragment WHERE $whereFragment".command
  }

  def commandIn: W = where.elt

  def execute[M[_]: MonadCancelThrow](s: Session[M])(implicit origin: Origin): M[Completion] =
    s.prepare(toCommand).use(_.execute(commandIn))
}
