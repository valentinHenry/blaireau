// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.assignment

import blaireau.metas.Meta
import shapeless.tag.@@
import shapeless.{HList, HNil}

import scala.annotation.implicitNotFound

trait AssignableMeta[T, F <: HList, EF <: HList] extends Dynamic {
  // TODO macro: replace select dynamic by functions of the present fields (to help idea + the user)
  def selectDynamic(k: String)(implicit
    @implicitNotFound(
      s"The field is either not present in the object or the object is optional (internal fields assignment of an optional object is not supported)"
    )
    s: AssignableSelector[F, Symbol @@ k.type]
  ): s.Out = s(fields)

  private[blaireau] def fields: F

  def :=(right: T): AssignmentAction[T] = assign(right)

  private[blaireau] def assign(t: T): AssignmentAction[T]
}

object AssignableMeta {
  def makeSelectable[T, F <: HList, MF <: HList, EF <: HList](
    meta: Meta.Aux[T, F, MF, EF]
  ): AssignableMeta[T, F, EF] =
    new AssignableMeta[T, F, EF] {
      override private[blaireau] val fields = meta.fields

      override private[blaireau] def assign(t: T): AssignmentAction[T] = meta.assignationApplier(meta.extract(t))
    }

  def makeUnselectable[T, F <: HList, MF <: HList, EF <: HList](
    meta: Meta.Aux[T, F, MF, EF]
  ): AssignableMeta[T, HNil, EF] =
    new AssignableMeta[T, HNil, EF] {
      override private[blaireau] def fields: HNil = HNil

      override private[blaireau] def assign(t: T): AssignmentAction[T] = meta.assignationApplier(meta.extract(t))
    }
}
