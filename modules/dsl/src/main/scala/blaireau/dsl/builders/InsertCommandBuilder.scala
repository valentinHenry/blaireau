// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.builders.InsertCommandBuilder.Ev.HasValues
import blaireau.utils.FragmentUtils
import cats.effect.MonadCancelThrow
import cats.implicits.catsSyntaxOptionId
import skunk.data.Completion
import skunk.implicits.toStringOps
import skunk.util.Origin
import skunk.{Command, Encoder, Session}

import scala.annotation.implicitNotFound

object InsertCommandBuilder {
  sealed trait Ev
  object Ev {
    sealed trait Empty     extends Ev
    sealed trait HasValues extends Ev

    type Requires = HasValues
  }

  def make[IC](
    tableName: String,
    insert: List[String],
    insertEncoder: Encoder[IC]
  ): InsertCommandBuilder[IC, Ev.Empty] =
    new InsertCommandBuilder[IC, Ev.Empty](tableName, insert, insertEncoder, None)
}

final class InsertCommandBuilder[IC, Ev](
  tableName: String,
  insert: List[String],
  insertEncoder: Encoder[IC],
  insertValues: Option[IC]
) {
  def value(value: IC): InsertCommandBuilder[IC, Ev with HasValues] =
    new InsertCommandBuilder[IC, Ev with HasValues](tableName, insert, insertEncoder.values, value.some)

  def values(values: List[IC]): InsertCommandBuilder[List[IC], Ev with HasValues] =
    new InsertCommandBuilder[List[IC], Ev with HasValues](
      tableName,
      insert,
      insertEncoder.values.list(values.size),
      values.some
    )

  def toCommand: Command[IC] = {
    val fields         = insert.mkString(",")
    val insertFragment = FragmentUtils.const(s"INSERT INTO $tableName($fields)")

    sql"$insertFragment VALUES $insertEncoder".command
  }

  def commandIn(implicit
    @implicitNotFound("The command does not have specified inserted values.") ev: Ev <:< HasValues
  ): IC =
    getOrISE(insertValues)

  def execute[F[_]: MonadCancelThrow](s: Session[F])(implicit
    @implicitNotFound("The command does not have specified inserted values.") ev: Ev <:< HasValues,
    origin: Origin
  ): F[Completion] =
    s.prepare(toCommand).use(_.execute(commandIn))

  private[this] def getOrISE[A](o: Option[A]): A =
    o.getOrElse(throw new IllegalStateException("Values not found - This should not happen."))

}
