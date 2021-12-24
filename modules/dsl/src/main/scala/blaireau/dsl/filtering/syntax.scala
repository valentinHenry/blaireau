// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.dsl.filtering.BooleanAction._
import blaireau.metas.{Meta, MetaField, OptionalMetaField}
import shapeless.HList
import shapeless.ops.hlist.{LeftReducer, Mapper}
import skunk.implicits.toIdOps
import skunk.util.Twiddler
import skunk.~

import scala.annotation.unused

trait MetaFieldBooleanSyntax {
  import MetaFieldOps._
  implicit final def metaEltOpsSyntax[T, F <: HList, MF <: HList, EF <: HList, OEF <: HList](
    m: Meta.Aux[T, F, MF, EF, OEF]
  ): MetaEltOps[T, F, MF, EF, OEF] =
    new MetaEltOps[T, F, MF, EF, OEF](m)

//  implicit final def optionalMetaEltOpsSyntax[
//    T,
//    MF <: HList,
//    EF <: HList,
//    IF <: HList,
//    IMF <: HList,
//    IEF <: HList,
//    IOEF <: HList
//  ](m: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF, IOEF]): OptionalMetaEltOps[T, MF, EF, IF, IMF, IEF, IOEF] =
//    new OptionalMetaEltOps(m)

  implicit final def optionMetaFieldSyntax[T](f: OptionalMetaField[T]): OptionMetaFieldOp[T] =
    new OptionMetaFieldOp[T](f)

  implicit final def metaFieldSyntax[T](f: MetaField[T]): MetaFieldOps[T] =
    new MetaFieldOps[T](f)

  implicit final def numericFieldSyntax[T](f: MetaField[T])(implicit @unused ev: Numeric[T]): NumericFieldOps[T] =
    new NumericFieldOps[T](f)

  implicit final def stringFieldSyntax(f: MetaField[String]): StringFieldOps[String] =
    new StringFieldOps[String](f)
}

object MetaFieldOps {
  class MetaEltOps[T, F <: HList, MF <: HList, EF <: HList, OEF <: HList](meta: Meta.Aux[T, F, MF, EF, OEF]) {
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

//  class OptionalMetaEltOps[
//    T,
//    MF <: HList,
//    EF <: HList,
//    IF <: HList,
//    IMF <: HList,
//    IEF <: HList,
//    IOEF <: HList
//  ](meta: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF, IOEF]) {
//    def ===[MEF <: HList, LRO, UF](right: T)(implicit
//      m: Mapper.Aux[booleanEqAndApplier.type, EF, MEF],
//      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
//      ev: LRO =:= IdBooleanAction[UF],
//      tw: Twiddler.Aux[T, UF]
//    ): IdBooleanAction[T] =
//      BooleanAction.booleanEqAnd(meta, right)
//
//    def =~=[MEF <: HList, LRO, UF](right: T)(implicit
//      m: Mapper.Aux[booleanEqOrApplier.type, EF, MEF],
//      r: LeftReducer.Aux[MEF, actionBooleanOrFolder.type, LRO],
//      ev: LRO =:= IdBooleanAction[UF],
//      tw: Twiddler.Aux[T, UF]
//    ): IdBooleanAction[T] =
//      BooleanAction.booleanEqOr(meta, right)
//
//    def =!=[MEF <: HList, LRO, UF](right: T)(implicit
//      m: Mapper.Aux[booleanNEqAndApplier.type, EF, MEF],
//      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
//      ev: LRO =:= IdBooleanAction[UF],
//      tw: Twiddler.Aux[T, UF]
//    ): IdBooleanAction[T] =
//      BooleanAction.booleanNEqAnd(meta, right)
//
//    def <>[MEF <: HList, LRO, UF](right: T)(implicit
//      m: Mapper.Aux[booleanNEqOrApplier.type, EF, MEF],
//      r: LeftReducer.Aux[MEF, actionBooleanOrFolder.type, LRO],
//      ev: LRO =:= IdBooleanAction[UF],
//      tw: Twiddler.Aux[T, UF]
//    ): IdBooleanAction[T] =
//      BooleanAction.booleanNEqOr(meta, right)
//  }

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

  class OptionMetaFieldOp[T](mf: OptionalMetaField[T]) extends MetaFieldBooleanSyntax {
    private[this] def unVoid[CO, O](a: BooleanAction[skunk.Void ~ CO, skunk.Void ~ O]): BooleanAction[CO, O] =
      BooleanAction.imap(a)(_._2)(_._2)(skunk.Void ~ _)(o => a.to(skunk.Void ~ o)._2)

    def contains(t: T): BooleanAction[Option[T], T] = BooleanOptionContains(mf.sqlName, mf.codec, t)
    def isEmpty: BooleanOptionIsEmpty               = BooleanOptionIsEmpty(mf.sqlName)
    def isDefined: BooleanOptionIsDefined           = BooleanOptionIsDefined(mf.sqlName)
    def exists[CO, O](f: MetaField[T] => BooleanAction[CO, O]): BooleanAction[CO, O] =
      unVoid(isDefined && f(mf.internal))
    def forall[CO, O](f: MetaField[T] => BooleanAction[CO, O]): BooleanAction[CO, O] =
      unVoid(isEmpty || f(mf.internal))
  }
}
