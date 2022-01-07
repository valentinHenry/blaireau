// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.dsl.filtering.BooleanAction._
import blaireau.metas.{MetaField, OptionalMetaField}
import blaireau.utils.FragmentUtils
import skunk.implicits.toIdOps
import skunk.{Void, ~}

import scala.annotation.unused

trait MetaFieldBooleanSyntax {
  import MetaFieldOps._

  implicit final def optionMetaFieldSyntax[T](f: OptionalMetaField[T]): OptionMetaFieldOp[T] =
    new OptionMetaFieldOp[T](f)

  implicit final def metaFieldSyntax[T](f: MetaField[T]): EqualityFieldOps[T] =
    new EqualityFieldOps[T](f)

  implicit final def numericFieldSyntax[T](f: MetaField[T])(implicit @unused ev: Numeric[T]): ComparableFieldOps[T] =
    new ComparableFieldOps[T](f)

  implicit final def stringFieldSyntax(f: MetaField[String]): StringFieldOps[String] =
    new StringFieldOps[String](f)

  implicit final def booleanFieldSyntax(f: MetaField[Boolean]): BooleanFieldOps[Boolean] =
    new BooleanFieldOps[Boolean](f)

  implicit final def temporalFieldSyntax[T](f: MetaField[T])(implicit @unused ev: Temporal[T]): ComparableFieldOps[T] =
    new ComparableFieldOps[T](f)

  implicit def asBooleanAction(f: MetaField[Boolean]): BooleanAction[Void] = f.asBool
}

object MetaFieldOps {
  private[this] def unVoid[CO, O](a: BooleanAction[Void ~ O]): BooleanAction[O] =
    BooleanAction.imap(a)(_._2)(Void ~ _)

  class EqualityFieldOps[T](f: MetaField[T]) {
    def ===(right: T): BooleanEq[T] =
      BooleanEq(f, right)

    def =~=(right: T): BooleanEq[T] =
      this === right

    def =!=(right: T): BooleanNEq[T] =
      this <> right

    def <>(right: T): BooleanNEq[T] =
      BooleanNEq(f, right)
  }

  class ComparableFieldOps[T](f: MetaField[T]) {
    def >=(right: T): BooleanGtEq[T] =
      BooleanGtEq(f, right)

    def >(right: T): BooleanGt[T] =
      BooleanGt(f, right)

    def <=(right: T): BooleanLtEq[T] =
      BooleanLtEq(f, right)

    def <(right: T): BooleanLt[T] =
      BooleanLt(f, right)
  }

  class StringFieldOps[T](f: MetaField[T]) {
    def >=(right: T): BooleanGtEq[T] =
      BooleanGtEq(f, right)

    def >(right: T): BooleanGt[T] =
      BooleanGt(f, right)

    def <=(right: T): BooleanLtEq[T] =
      BooleanLtEq(f, right)

    def <(right: T): BooleanLt[T] =
      BooleanLt(f, right)

    def like(right: T): BooleanLike[T] =
      BooleanLike(f, right)
  }

  class BooleanFieldOps[T](f: MetaField[T]) {
    def asBool: BooleanAction[Void] =
      ForgedBoolean(
        Void.codec,
        Void,
        picker => FragmentUtils.const(s"${picker.get(f)}")
      )

  }

  class OptionMetaFieldOp[T](mf: OptionalMetaField[T]) extends MetaFieldBooleanSyntax {
    def contains(t: T): BooleanAction[T] = mf.internal === t

    def isEmpty: BooleanOptionIsEmpty = BooleanOptionIsEmpty(mf)

    def isDefined: BooleanOptionIsDefined = BooleanOptionIsDefined(mf)

    def exists[O](f: MetaField[T] => BooleanAction[O]): BooleanAction[O] =
      unVoid(isDefined && f(mf.internal))

    def forall[O](f: MetaField[T] => BooleanAction[O]): BooleanAction[O] =
      unVoid(isEmpty || f(mf.internal))
  }
}
