// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.metas.MetaField

import java.util.UUID

class FieldNamePicker(mappedFields: Map[UUID, String], idMapping: Map[UUID, UUID]) {
  def get(mf: MetaField[_]): String =
    mappedFields
      .get(mf.id)
      .orElse(idMapping.get(mf.id).flatMap(mappedFields.get))
      .getOrElse(mf.sqlName)
}

object FieldNamePicker {
  def empty: FieldNamePicker = new FieldNamePicker(Map.empty, Map.empty)
}
