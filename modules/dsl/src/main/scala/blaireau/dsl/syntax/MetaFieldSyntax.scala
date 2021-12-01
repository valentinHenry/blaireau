// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.syntax

import blaireau.MetaField
import blaireau.dsl.table.Action

import scala.language.implicitConversions

object fields extends MetaFieldSyntax

trait MetaFieldSyntax {
  implicit def toOps(f: MetaField): MetaFieldOps = new MetaFieldOps(f)
}

final class MetaFieldOps(val field: MetaField) {
  def ==(right: field.FieldType): Action.BooleanEq[field.FieldType] =
    Action.BooleanEq(field.sqlName, field.codec, right)

  def !=(right: field.FieldType): Action.BooleanNEq[field.FieldType] =
    Action.BooleanNEq(field.sqlName, field.codec, right)
}
