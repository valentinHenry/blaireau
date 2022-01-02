// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.dsl.actions.AllVoid
import blaireau.dsl.filtering.BooleanAction._
import blaireau.metas.{Meta, MetaField, OptionalMeta, OptionalMetaField}
import blaireau.utils.FragmentUtils
import shapeless.HList
import shapeless.ops.hlist.{LeftReducer, Mapper}
import skunk.implicits.toIdOps
import skunk.util.Twiddler
import skunk.{Void, ~}

import scala.annotation.unused

trait MetaFieldBooleanSyntax {

  import MetaFieldOps._

  implicit final def metaEltOpsSyntax[T, F <: HList, MF <: HList, EF <: HList](
    m: Meta.Aux[T, F, MF, EF]
  ): MetaEltOps[T, F, MF, EF] =
    new MetaEltOps[T, F, MF, EF](m)

  implicit final def optionalMetaEltOpsSyntax[
    T,
    MF <: HList,
    EF <: HList,
    IF <: HList,
    IMF <: HList,
    IEF <: HList
  ](m: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF]): OptionalMetaEltOps[T, MF, EF, IF, IMF, IEF] =
    new OptionalMetaEltOps(m)

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

  class MetaEltOps[T, F <: HList, MF <: HList, EF <: HList](meta: Meta.Aux[T, F, MF, EF]) {
    def ===[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[booleanEqAndApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): BooleanAction[T] =
      BooleanAction.booleanEqAnd(meta, right)

    def =~=[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[booleanEqOrApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): BooleanAction[T] =
      BooleanAction.booleanEqOr(meta, right)

    def =!=[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[booleanNEqAndApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): BooleanAction[T] =
      BooleanAction.booleanNEqAnd(meta, right)

    def <>[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[booleanNEqOrApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): BooleanAction[T] =
      BooleanAction.booleanNEqOr(meta, right)
  }

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

  class OptionalMetaEltOps[
    T,
    MF <: HList,
    EF <: HList,
    IF <: HList,
    IMF <: HList,
    IEF <: HList
  ](meta: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF])
    extends MetaFieldBooleanSyntax {
    def ===[MEF <: HList, LRO, UF](right: Option[T])(implicit
      m: Mapper.Aux[booleanEqAndApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[Option[T], UF]
    ): BooleanAction[Option[T]] =
      BooleanAction.booleanEqAnd(meta, right)

    def =~=[MEF <: HList, LRO, UF](right: Option[T])(implicit
      m: Mapper.Aux[booleanEqOrApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[Option[T], UF]
    ): BooleanAction[Option[T]] =
      BooleanAction.booleanEqOr(meta, right)

    def =!=[MEF <: HList, LRO, UF](right: Option[T])(implicit
      m: Mapper.Aux[booleanNEqAndApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[Option[T], UF]
    ): BooleanAction[Option[T]] =
      BooleanAction.booleanNEqAnd(meta, right)

    def <>[MEF <: HList, LRO, UF](right: Option[T])(implicit
      m: Mapper.Aux[booleanNEqOrApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[Option[T], UF]
    ): BooleanAction[Option[T]] =
      BooleanAction.booleanNEqOr(meta, right)

    def contains[MEF <: HList, LRO, UF](t: T)(implicit
      m: Mapper.Aux[booleanEqAndApplier.type, IEF, MEF],
      r: LeftReducer.Aux[MEF, actionBooleanAndFolder.type, LRO],
      ev: LRO =:= BooleanAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): BooleanAction[T] =
      meta.internal === t

    def exists[O, MMF <: HList, LRO, V](f: Meta.Aux[T, IF, IMF, IEF] => BooleanAction[O])(implicit
      m: Mapper.Aux[booleanNotEmptyApplier.type, MF, MMF],
      r: LeftReducer.Aux[MMF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= BooleanAction[V],
      uv: AllVoid[V]
    ): BooleanAction[O] =
      unVoid(isDefined && f(meta.internal))

    def isDefined[MMF <: HList, LRO, V](implicit
      m: Mapper.Aux[booleanNotEmptyApplier.type, MF, MMF],
      r: LeftReducer.Aux[MMF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= BooleanAction[V],
      uv: AllVoid[V]
    ): BooleanAction[Void] = {
      val act: BooleanAction[V] = meta.metaFields.map(booleanNotEmptyApplier).reduceLeft(actionBooleanOrFolder)

      ForgedBoolean(
        Void.codec,
        Void,
        picker => act.toFragment(picker).contramap(uv.from)
      )
    }

    def isEmpty[MMF <: HList, LRO, V](implicit
      m: Mapper.Aux[booleanNotEmptyApplier.type, MF, MMF],
      r: LeftReducer.Aux[MMF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= BooleanAction[V],
      uv: AllVoid[V]
    ): BooleanAction[Void] = !isDefined

    def forall[O, MMF <: HList, LRO, V](f: Meta.Aux[T, IF, IMF, IEF] => BooleanAction[O])(implicit
      m: Mapper.Aux[booleanNotEmptyApplier.type, MF, MMF],
      r: LeftReducer.Aux[MMF, actionBooleanOrFolder.type, LRO],
      ev: LRO =:= BooleanAction[V],
      uv: AllVoid[V]
    ): BooleanAction[O] =
      unVoid(isEmpty || f(meta.internal))
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
