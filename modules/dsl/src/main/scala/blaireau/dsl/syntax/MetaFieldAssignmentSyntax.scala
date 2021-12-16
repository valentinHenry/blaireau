// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.syntax

import blaireau.dsl.actions.AssignmentAction._
import blaireau.metas.MetaField

import scala.language.implicitConversions

trait MetaFieldAssignmentSyntax {
  import MetaFieldAssignmentOps._

  implicit final def assignmentOpsSyntax[T](mf: MetaField[T]): MetaFieldOps[T] =
    new MetaFieldOps[T](mf)

  implicit final def numericAssignmentOpsSyntax[T: Numeric](mf: MetaField[T]): MetaFieldNumericOps[T] =
    new MetaFieldNumericOps[T](mf: MetaField[T])
}

object MetaFieldAssignmentOps {
  final class MetaFieldOps[T](mf: MetaField[T]) {
    def :=(right: T): AssignmentOp[T] =
      AssignmentOp[T](mf.sqlName, mf.codec, right)
  }

  final class MetaFieldNumericOps[T](mf: MetaField[T]) {
    def +=(right: T): AssignmentIncr[T] =
      AssignmentIncr[T](mf.sqlName, mf.codec, right)

    def -=(right: T): AssignmentDecr[T] =
      AssignmentDecr[T](mf.sqlName, mf.codec, right)
  }
}
