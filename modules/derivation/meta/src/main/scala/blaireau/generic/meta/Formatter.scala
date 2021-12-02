// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.meta

import enumeratum.values.{StringEnum, StringEnumEntry}

sealed abstract class FieldFormat(val value: String) extends StringEnumEntry
object FieldFormat extends StringEnum[FieldFormat] {
  override def values: IndexedSeq[FieldFormat] = findValues

  final case object CamelCase extends FieldFormat("camelCase")
  final case object SnakeCase extends FieldFormat("snake_case")
}

object Formatter {
  def of(format: FieldFormat): Formatter =
    format match {
      case FieldFormat.CamelCase => identity
      case FieldFormat.SnakeCase => _.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase()
    }
}
