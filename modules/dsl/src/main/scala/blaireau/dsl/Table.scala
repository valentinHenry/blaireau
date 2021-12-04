// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.metas.Meta

object Table {
//  def apply[T](name: String)(implicit m: Meta[T]): Table[T] = new Table[T](name, Meta[T])
}

class Table[T](val name: String, val meta: Meta[T])

/** trait Table{T_type_name}[T] {
  *  def tableName: String
  *  def meta: Meta[T]
  *
  *  <--->
  *  Generated object field getters named after the derived meta used in the dsl
  *  <--->
  * }
  *
  * Usage:
  * val table: TableBlaireau[Blaireau] = Table.tableOf[Blaireau]("blaireaux")
  */
