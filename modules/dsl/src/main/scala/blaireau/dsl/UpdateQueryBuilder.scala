// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.dsl.actions.{AssignmentAction, BooleanAction}
import blaireau.metas.{Meta, MetaElt}
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import shapeless.HList
import skunk.data.Completion
import skunk.implicits.toStringOps
import skunk.{Command, Session, ~}

class UpdateQueryBuilder[T, F <: HList, MF <: HList, U, W](
  tableName: String,
  meta: Meta.Aux[T, F, MF],
  updatedFields: AssignmentAction[U],
  where: BooleanAction[W]
) {
  def where[A](f: MetaElt.Aux[T, F, MF] => BooleanAction[A]): UpdateQueryBuilder[T, F, MF, U, A] =
    new UpdateQueryBuilder(tableName, meta, updatedFields, f(meta))

  def whereAnd[A](f: MetaElt.Aux[T, F, MF] => BooleanAction[A]): UpdateQueryBuilder[T, F, MF, U, (W, A)] =
    new UpdateQueryBuilder(tableName, meta, updatedFields, where && f(meta))

  def whereOr[A](f: MetaElt.Aux[T, F, MF] => BooleanAction[A]): UpdateQueryBuilder[T, F, MF, U, (W, A)] =
    new UpdateQueryBuilder(tableName, meta, updatedFields, where || f(meta))

  def toCommand: Command[U ~ W] = {
    val updateFragment     = FragmentUtils.const(s"UPDATE TABLE $tableName")
    val assignmentFragment = updatedFields.toFragment
    val whereFragment      = where.toFragment
    sql"$updateFragment SET ($assignmentFragment) WHERE $whereFragment".command
  }

  def commandIn: U ~ W = (updatedFields.elt, where.elt)

  def execute[M[_]: MonadCancelThrow](s: Session[M]): M[Completion] =
    s.prepare(toCommand).use(_.execute(commandIn))
}
