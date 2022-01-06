// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.selection

import blaireau.metas.{Meta, MetaField, OptionalMetaField}
import shapeless.HList
import shapeless.ops.record.Selector

trait SelectableSelector[F <: HList, K] {
  type Out

  def apply(s: F): Out
}

object SelectableSelector {
  type Aux[F <: HList, K, O] = SelectableSelector[F, K] {
    type Out = O
  }

  def apply[F <: HList, K, O](m: F => O): SelectableSelector.Aux[F, K, O] =
    new SelectableSelector[F, K] {
      override final type Out = O

      override final def apply(s: F): O = m(s)
    }

  implicit def metaSelector[F <: HList, K, SO, OT, OF <: HList, OMF <: HList, OEF <: HList](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO <:< Meta.Aux[OT, OF, OMF, OEF]
  ): SelectableSelector.Aux[F, K, SelectableMeta[OT, OF, OMF]] =
    SelectableSelector(f => SelectableMeta.make(s(f)))

  implicit def fieldSelector[F <: HList, K, SO, T](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO =:= MetaField[T]
  ): SelectableSelector.Aux[F, K, MetaField[T]] = SelectableSelector(s(_))

  implicit def optionalFieldSelector[F <: HList, K, SO, T](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO =:= OptionalMetaField[T]
  ): SelectableSelector.Aux[F, K, OptionalMetaField[T]] = SelectableSelector(s(_))
}
