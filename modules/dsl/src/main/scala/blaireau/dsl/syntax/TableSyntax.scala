// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.syntax

import blaireau.dsl.table.{SelectQuery, Table}
import skunk.Void

import scala.language.implicitConversions

object table extends TableSyntax

trait TableSyntax {
  implicit def tableSyntaxOps[A](table: Table[A]): TableSyntaxOps[A] =
    new TableSyntaxOps[A](table)
}

class TableSyntaxOps[A](table: Table[A]) {
  def select: SelectQuery[A, Void] = SelectQuery.make(table)
}
