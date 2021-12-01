// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.table

import blaireau.Meta

class Table[T](val name: String, val meta: Meta[T])

object Table {
  def apply[T: Meta](name: String): Table[T] = new Table[T](name, Meta[T])
}
