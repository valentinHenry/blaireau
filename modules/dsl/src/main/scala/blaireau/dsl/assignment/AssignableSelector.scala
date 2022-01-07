// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.assignment

import blaireau.metas.{Meta, MetaField, OptionalMetaField}
import shapeless.ops.record.Selector
import shapeless.{<:!<, HList, HNil}

import scala.annotation.unused

trait AssignableSelector[F <: HList, K] {
  type Out

  def apply(s: F): Out
}

object AssignableSelector {
  type Aux[F <: HList, K, O] = AssignableSelector[F, K] {
    type Out = O
  }

  def apply[F <: HList, K, O](m: F => O): AssignableSelector.Aux[F, K, O] =
    new AssignableSelector[F, K] {
      override final type Out = O

      override final def apply(s: F): O = m(s)
    }

  implicit def metaSelector[F <: HList, K, SO, OT, OF <: HList, OMF <: HList, OEF <: HList](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO <:< Meta.Aux[OT, OF, OMF, OEF],
    @unused evNotOption: OT <:!< Option[_]
  ): AssignableSelector.Aux[F, K, AssignableMeta[OT, OF, OEF]] =
    AssignableSelector(f => AssignableMeta.makeSelectable(s(f)))

  implicit def optionalMetaSelector[F <: HList, K, SO, OT, OF <: HList, OMF <: HList, OEF <: HList](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO <:< Meta.Aux[Option[OT], OF, OMF, OEF]
  ): AssignableSelector.Aux[F, K, AssignableMeta[Option[OT], HNil, OEF]] =
    AssignableSelector(f => AssignableMeta.makeUnselectable(s(f)))

  implicit def fieldSelector[F <: HList, K, SO, T](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO =:= MetaField[T]
  ): AssignableSelector.Aux[F, K, MetaField[T]] = AssignableSelector(s(_))

  implicit def optionalFieldSelector[F <: HList, K, SO, T](implicit
    s: Selector.Aux[F, K, SO],
    ev: SO =:= OptionalMetaField[T]
  ): AssignableSelector.Aux[F, K, OptionalMetaField[T]] = AssignableSelector(s(_))
}
