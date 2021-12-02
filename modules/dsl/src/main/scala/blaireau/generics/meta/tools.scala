// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.meta

import blaireau.Meta

private[meta] object tools {
  def assertFieldsIntegrity[T](typeName: String, meta: Meta[T]): Unit = {
    val groupedFields = meta.fields.groupBy(identity)
    assert(
      groupedFields.keys.size == meta.fields.size, {
        val duplicatedFields = groupedFields.filter(_._2.size >= 2).keys
        s"""\nFailed to create Meta for class $typeName. It has duplicated fields:
           |${duplicatedFields.mkString(" - ", "\n - ", "")}""".stripMargin
      }
    )
  }
}
