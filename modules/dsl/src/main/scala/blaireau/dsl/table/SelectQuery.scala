// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.table

import blaireau.DbElt
import blaireau.dsl.table.Action.BooleanOp
import skunk._
import skunk.syntax.StringContextOps
import skunk.syntax.all._
import skunk.util.Origin

object SelectQuery {
  def make[Entity](table: Table[Entity]): SelectQuery[Entity, Void] =
    new SelectQuery(table, BooleanOp.empty)
}

class SelectQuery[Entity, In](
  table: Table[Entity],
  where: BooleanOp[In]
) {
  def filter[A](f: DbElt[Entity] => BooleanOp[A]): SelectQuery[Entity, In ~ A] =
    new SelectQuery[Entity, In ~ A](table, where && f(table.meta))

  def toFragment: Fragment[In] = {
    val fields: String = table.meta.fields.map(_.name).mkString(",")

    val selectFrom: Fragment[Void] = StringContextOps.fragmentFromParts(
      List(
        StringContextOps.Str(s"SELECT $fields FROM ${table.name}")
      ),
      Void.codec,
      Origin.unknown
    )

    sql"$selectFrom WHERE ${where.toFragment}"
  }
//
//  def cursor(implicit or: Origin)                 = ???
//  def stream(chunkSize: Int)(implicit or: Origin) = ???
//  def option(implicit or: Origin)                 = ???
//  def unique(implicit or: Origin)                 = ???
//  def pipe(chunkSize: Int)(implicit or: Origin)   = ???
}
