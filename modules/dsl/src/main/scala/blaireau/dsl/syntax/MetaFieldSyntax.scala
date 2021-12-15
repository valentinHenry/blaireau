// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.syntax

import blaireau.dsl.Action
import blaireau.metas.MetaField

import scala.language.implicitConversions

trait MetaFieldSyntax {
  import MetaFieldOps._
  implicit final def numericFieldSyntax[T: Numeric](f: MetaField[T]): NumericFieldOps[T] =
    new NumericFieldOps[T](f)

  implicit final def stringFieldSyntax(f: MetaField[String]): StringFieldOps[String] =
    new StringFieldOps[String](f)

}

object MetaFieldOps {
  class NumericFieldOps[T](f: MetaField[T]) {
    def ===(right: T): Action.BooleanEq[T] =
      Action.BooleanEq(f.sqlName, f.codec, right)

    def <>(right: T): Action.BooleanNEq[T] =
      Action.BooleanNEq(f.sqlName, f.codec, right)

    def >=(right: T): Action.BooleanGtEq[T] =
      Action.BooleanGtEq(f.sqlName, f.codec, right)

    def >(right: T): Action.BooleanGt[T] =
      Action.BooleanGt(f.sqlName, f.codec, right)

    def <=(right: T): Action.BooleanLtEq[T] =
      Action.BooleanLtEq(f.sqlName, f.codec, right)

    def <(right: T): Action.BooleanLt[T] =
      Action.BooleanLt(f.sqlName, f.codec, right)
  }

  class StringFieldOps[T](f: MetaField[T]) {
    def ===(right: T): Action.BooleanEq[T] =
      Action.BooleanEq(f.sqlName, f.codec, right)

    def >=(right: T): Action.BooleanGtEq[T] =
      Action.BooleanGtEq(f.sqlName, f.codec, right)

    def >(right: T): Action.BooleanGt[T] =
      Action.BooleanGt(f.sqlName, f.codec, right)

    def <=(right: T): Action.BooleanLtEq[T] =
      Action.BooleanLtEq(f.sqlName, f.codec, right)

    def <(right: T): Action.BooleanLt[T] =
      Action.BooleanLt(f.sqlName, f.codec, right)

    def like(right: T): Action.BooleanLike[T] =
      Action.BooleanLike(f.sqlName, f.codec, right)
  }
}
