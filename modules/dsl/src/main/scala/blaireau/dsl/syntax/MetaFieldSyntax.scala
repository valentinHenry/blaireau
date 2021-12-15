// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.syntax

import blaireau.dsl.actions.BooleanAction._
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
    def ===(right: T): BooleanEq[T] =
      BooleanEq(f.sqlName, f.codec, right)

    def <>(right: T): BooleanNEq[T] =
      BooleanNEq(f.sqlName, f.codec, right)

    def >=(right: T): BooleanGtEq[T] =
      BooleanGtEq(f.sqlName, f.codec, right)

    def >(right: T): BooleanGt[T] =
      BooleanGt(f.sqlName, f.codec, right)

    def <=(right: T): BooleanLtEq[T] =
      BooleanLtEq(f.sqlName, f.codec, right)

    def <(right: T): BooleanLt[T] =
      BooleanLt(f.sqlName, f.codec, right)
  }

  class StringFieldOps[T](f: MetaField[T]) {
    def ===(right: T): BooleanEq[T] =
      BooleanEq(f.sqlName, f.codec, right)

    def >=(right: T): BooleanGtEq[T] =
      BooleanGtEq(f.sqlName, f.codec, right)

    def >(right: T): BooleanGt[T] =
      BooleanGt(f.sqlName, f.codec, right)

    def <=(right: T): BooleanLtEq[T] =
      BooleanLtEq(f.sqlName, f.codec, right)

    def <(right: T): BooleanLt[T] =
      BooleanLt(f.sqlName, f.codec, right)

    def like(right: T): BooleanLike[T] =
      BooleanLike(f.sqlName, f.codec, right)
  }
}
