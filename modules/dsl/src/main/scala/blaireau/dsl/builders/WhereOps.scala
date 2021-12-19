// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.actions.BooleanAction
import blaireau.metas.Meta
import shapeless.HList

trait WhereOps[T, F <: HList, MF <: HList, EF <: HList, W] {
  private[blaireau] def where: BooleanAction[W]
  private[blaireau] def meta: Meta.Aux[T, F, MF, EF]

  type SelfT[T0, F0 <: HList, MF0 <: HList, EF0 <: HList, W0]

  def withWhere[NW](newWhere: BooleanAction[NW]): SelfT[T, F, MF, EF, NW]

  final def where[A](f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]): SelfT[T, F, MF, EF, A] =
    withWhere(f(meta))

  final def whereAnd[A](f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]): SelfT[T, F, MF, EF, (W, A)] =
    withWhere(where && f(meta))

  final def whereOr[A](f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]): SelfT[T, F, MF, EF, (W, A)] =
    withWhere(where || f(meta))
}
