// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.metas.{Meta, MetaField, OptionalMeta, OptionalMetaField}
import shapeless.ops.record.Selector
import shapeless.{<:!<, HList}

import scala.annotation.nowarn

trait FilterableSelector[F <: HList, K] {
  type Out

  def apply(s: F): Out
}

object FilterableSelector {
  type Aux[F <: HList, K, O] = FilterableSelector[F, K] {
    type Out = O
  }

  def apply[F <: HList, K, O](m: F => O): FilterableSelector.Aux[F, K, O] =
    new FilterableSelector[F, K] {
      override final type Out = O

      override final def apply(s: F): O = m(s)
    }

  @nowarn("cat=unused")
  implicit def metaSelector[F <: HList, K, SO, OT, OF <: HList, OMF <: HList, OEF <: HList](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO <:< Meta.Aux[OT, OF, OMF, OEF],
    evNotOption: OT <:!< Option[_]
  ): FilterableSelector.Aux[F, K, FilterableMeta[OT, OF, OEF]] =
    FilterableSelector(f => FilterableMeta.make(s(f)))

  implicit def optionalMetaSelector[
    F <: HList,
    K,
    SO,
    OT,
    OMF <: HList,
    OEF <: HList,
    OIF <: HList,
    OIMF <: HList,
    OIEF <: HList
  ](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO <:< OptionalMeta.Aux[OT, OMF, OEF, OIF, OIMF, OIEF]
  ): FilterableSelector.Aux[F, K, OptionalFilterableMeta[OT, OMF, OEF, OIF, OIEF]] =
    FilterableSelector(f => FilterableMeta.makeOptional(s(f)))

  implicit def fieldSelector[F <: HList, K, SO, T](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO =:= MetaField[T]
  ): FilterableSelector.Aux[F, K, MetaField[T]] = FilterableSelector(s(_))

  implicit def optionalFieldSelector[F <: HList, K, SO, T](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO =:= OptionalMetaField[T]
  ): FilterableSelector.Aux[F, K, OptionalMetaField[T]] = FilterableSelector(s(_))
}
