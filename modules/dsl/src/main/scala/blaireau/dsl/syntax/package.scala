package blaireau.dsl

import blaireau.MetaField
import blaireau.dsl.table.Action

import scala.language.implicitConversions

package object syntax {
//  implicit final def numericFieldSyntax[T: Numeric](f: MetaField.Aux[T]): NumericFieldOps[T] =
//    new NumericFieldOps[T](f)
//
//  class NumericFieldOps[T](f: MetaField.Aux[T]) {
//    def ===(right: T): Action.BooleanEq[T] =
//      Action.BooleanEq(f.sqlName, f.codec, right)
//
//    def <>(right: T): Action.BooleanNEq[T] =
//      Action.BooleanNEq(f.sqlName, f.codec, right)
//
//    def >=(right: T): Action.BooleanGtEq[T] =
//      Action.BooleanGtEq(f.sqlName, f.codec, right)
//
//    def >(right: T): Action.BooleanGt[T] =
//      Action.BooleanGt(f.sqlName, f.codec, right)
//
//    def <=(right: T): Action.BooleanLtEq[T] =
//      Action.BooleanLtEq(f.sqlName, f.codec, right)
//
//    def <(right: T): Action.BooleanLt[T] =
//      Action.BooleanLt(f.sqlName, f.codec, right)
//  }
//
//  class StringFieldOps[T](f: MetaField.Aux[T]) {
//    def ===(right: T): Action.BooleanEq[T] =
//      Action.BooleanEq(f.sqlName, f.codec, right)
//
//    def >=(right: T): Action.BooleanGtEq[T] =
//      Action.BooleanGtEq(f.sqlName, f.codec, right)
//
//    def >(right: T): Action.BooleanGt[T] =
//      Action.BooleanGt(f.sqlName, f.codec, right)
//
//    def <=(right: T): Action.BooleanLtEq[T] =
//      Action.BooleanLtEq(f.sqlName, f.codec, right)
//
//    def <(right: T): Action.BooleanLt[T] =
//      Action.BooleanLt(f.sqlName, f.codec, right)
//  }
}
