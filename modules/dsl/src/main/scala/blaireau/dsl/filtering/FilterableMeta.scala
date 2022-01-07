// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.dsl.actions.AllVoid
import blaireau.metas.{Meta, OptionalMeta}
import shapeless.ops.hlist.{LeftReducer, Mapper}
import shapeless.tag.@@
import shapeless.{HList, HNil}
import skunk.implicits.toIdOps
import skunk.{Void, ~}

import scala.annotation.implicitNotFound

trait FilterableMeta[T, F <: HList, EF <: HList] extends Dynamic {
  // TODO macro: replace select dynamic by functions of the present fields (to help idea + the user)
  def selectDynamic(k: String)(implicit
    @implicitNotFound(
      s"The field is either not present in the object or the object is optional (internal fields boolean action of an optional object is not supported, unless done with .exists, .contains etc.)"
    )
    s: FilterableSelector[F, Symbol @@ k.type]
  ): s.Out = s(fields)

  def semiEq(t: T): BooleanAction[T]

  final def =~=(t: T): BooleanAction[T] = semiEq(t)

  def fullEq(t: T): BooleanAction[T]

  final def ===(t: T): BooleanAction[T] = fullEq(t)

  def neq(t: T): BooleanAction[T]

  final def <>(t: T): BooleanAction[T] = neq(t)

  def fullNeq(t: T): BooleanAction[T]

  final def =!=(t: T): BooleanAction[T] = fullNeq(t)

  private[blaireau] def fields: F
}

trait OptionalFilterableMeta[T, MF <: HList, EF <: HList, IF <: HList, IEF <: HList]
  extends FilterableMeta[Option[T], HNil, EF] {
  def contains[MEF <: HList, LRO, UF](t: T): BooleanAction[T] = internal === t

  def exists[O, MMF <: HList, LRO, V](f: FilterableMeta[T, IF, IEF] => BooleanAction[O])(implicit
    mapper: Mapper.Aux[booleanNotEmptyApplier.type, MF, MMF],
    reducer: LeftReducer.Aux[MMF, actionBooleanOrFolder.type, LRO],
    ev: LRO =:= BooleanAction[V],
    uv: AllVoid[V]
  ): BooleanAction[O] =
    unVoid(isDefined && f(internal))

  def forall[O, MMF <: HList, LRO, V](f: FilterableMeta[T, IF, IEF] => BooleanAction[O])(implicit
    mapper: Mapper.Aux[booleanNotEmptyApplier.type, MF, MMF],
    reducer: LeftReducer.Aux[MMF, actionBooleanOrFolder.type, LRO],
    ev: LRO =:= BooleanAction[V],
    uv: AllVoid[V]
  ): BooleanAction[O] =
    unVoid(isEmpty || f(internal))

  def isEmpty[MMF <: HList, LRO, V](implicit
    mapper: Mapper.Aux[booleanNotEmptyApplier.type, MF, MMF],
    reducer: LeftReducer.Aux[MMF, actionBooleanOrFolder.type, LRO],
    ev: LRO =:= BooleanAction[V],
    uv: AllVoid[V]
  ): BooleanAction[Void] = !isDefined

  def isDefined[MMF <: HList, LRO, V](implicit
    mapper: Mapper.Aux[booleanNotEmptyApplier.type, MF, MMF],
    reducer: LeftReducer.Aux[MMF, actionBooleanOrFolder.type, LRO],
    ev: LRO =:= BooleanAction[V],
    uv: AllVoid[V]
  ): BooleanAction[Void] = {
    val act: BooleanAction[V] = metaFields.map(booleanNotEmptyApplier).reduceLeft(actionBooleanOrFolder)

    ForgedBoolean(
      Void.codec,
      Void,
      picker => act.toFragment(picker).contramap(uv.from)
    )
  }

  private[this] def unVoid[CO, O](a: BooleanAction[Void ~ O]): BooleanAction[O] =
    BooleanAction.imap(a)(_._2)(Void ~ _)

  private[blaireau] def internal: FilterableMeta[T, IF, IEF]

  private[blaireau] def metaFields: MF
}

object FilterableMeta {
  def makeOptional[T, MF <: HList, EF <: HList, IF <: HList, IMF <: HList, IEF <: HList](
    meta: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF]
  ): OptionalFilterableMeta[T, MF, EF, IF, IEF] =
    new OptionalFilterableMeta[T, MF, EF, IF, IEF] {
      override private[blaireau] val fields: HNil = HNil

      override def semiEq(t: Option[T]): BooleanAction[Option[T]] =
        meta.booleanApplier.semiEq(meta.extract(t))

      override def fullEq(t: Option[T]): BooleanAction[Option[T]] =
        meta.booleanApplier.fullEq(meta.extract(t))

      override def neq(t: Option[T]): BooleanAction[Option[T]] =
        meta.booleanApplier.neq(meta.extract(t))

      override def fullNeq(t: Option[T]): BooleanAction[Option[T]] =
        meta.booleanApplier.fullNeq(meta.extract(t))

      override private[blaireau] def internal = make(meta.internal)

      override private[blaireau] def metaFields = meta.metaFields
    }

  def make[T, F <: HList, MF <: HList, EF <: HList](
    meta: Meta.Aux[T, F, MF, EF]
  ): FilterableMeta[T, F, EF] =
    new FilterableMeta[T, F, EF] {
      override private[blaireau] val fields = meta.fields

      override def semiEq(t: T): BooleanAction[T] = meta.booleanApplier.semiEq(meta.extract(t))

      override def fullEq(t: T): BooleanAction[T] = meta.booleanApplier.fullEq(meta.extract(t))

      override def neq(t: T): BooleanAction[T] = meta.booleanApplier.neq(meta.extract(t))

      override def fullNeq(t: T): BooleanAction[T] = meta.booleanApplier.fullNeq(meta.extract(t))
    }
}
