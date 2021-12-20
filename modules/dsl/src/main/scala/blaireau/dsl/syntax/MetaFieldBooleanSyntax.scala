// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.syntax

import blaireau.dsl.actions._
import blaireau.dsl.actions.BooleanAction._
import blaireau.metas.{Meta, MetaField}
import shapeless.HList
import shapeless.ops.hlist.{LeftReducer, Mapper}
import skunk.util.Twiddler

import scala.annotation.unused

trait MetaFieldBooleanSyntax {
  import MetaFieldOps._
  implicit final def metaEltOpsSyntax[T, F <: HList, MF <: HList, EF <: HList](
    m: Meta.Aux[T, F, MF, EF]
  ): MetaEltOps[T, F, MF, EF] =
    new MetaEltOps[T, F, MF, EF](m)

  implicit final def optionMetaFieldSyntax[T](f: MetaField[Option[T]]): OptionMetaFieldOp[T] =
    new OptionMetaFieldOp[T](f)

  implicit final def metaFieldSyntax[T](f: MetaField[T]): MetaFieldOps[T] =
    new MetaFieldOps[T](f)

  implicit final def numericFieldSyntax[T](f: MetaField[T])(implicit @unused ev: Numeric[T]): NumericFieldOps[T] =
    new NumericFieldOps[T](f)

  implicit final def stringFieldSyntax(f: MetaField[String]): StringFieldOps[String] =
    new StringFieldOps[String](f)
}

object MetaFieldOps {
  class MetaEltOps[T, F <: HList, MF <: HList, EF <: HList](meta: Meta.Aux[T, F, MF, EF]) {
    def ===[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[booleanEqAndApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
      ev: LRO =:= IdBooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): IdBooleanAction[T] =
      BooleanAction.booleanEqAnd(meta, right)

    def =~=[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[booleanEqOrApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= IdBooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): IdBooleanAction[T] =
      BooleanAction.booleanEqOr(meta, right)

    def =!=[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[booleanNEqAndApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
      ev: LRO =:= IdBooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): IdBooleanAction[T] =
      BooleanAction.booleanNEqAnd(meta, right)

    def <>[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[booleanNEqOrApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= IdBooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): IdBooleanAction[T] =
      BooleanAction.booleanNEqOr(meta, right)
  }

  class MetaFieldOps[T](f: MetaField[T]) {
    def ===(right: T): BooleanEq[T] =
      BooleanEq(f.sqlName, f.codec, right)

    def =~=(right: T): BooleanEq[T] =
      this === right

    def =!=(right: T): BooleanNEq[T] =
      this <> right

    def <>(right: T): BooleanNEq[T] =
      BooleanNEq(f.sqlName, f.codec, right)
  }

  class NumericFieldOps[T](f: MetaField[T]) {
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

  class OptionMetaFieldOp[T](mf: MetaField[Option[T]]) extends MetaFieldBooleanSyntax {
    def contains(t: T): BooleanAction[Option[T], T] = BooleanOptionContains(mf.sqlName, mf.codec, t)
    def isEmpty: BooleanOptionIsEmpty               = BooleanOptionIsEmpty(mf.sqlName)
    def isDefined: BooleanOptionIsDefined           = BooleanOptionIsDefined(mf.sqlName)
    // TODO
    // def exists[CO, O](f: MetaField[T] => BooleanAction[CO, O]): BooleanAction[CO, O] = ???
    // def forall[CO, O](f: MetaField[T] => BooleanAction[CO, O]): BooleanAction[CO, O] = ???
  }
}
