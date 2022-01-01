// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import blaireau.metas.Meta
import shapeless.HList
import skunk.~

trait WhereBuilder[T, F <: HList, MF <: HList, EF <: HList, W] {
  type SelfT[T0, F0 <: HList, MF0 <: HList, EF0 <: HList, W0]

  def withWhere[NW](newWhere: BooleanAction[NW]): SelfT[T, F, MF, EF, NW]

  final def where[A](f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]): SelfT[T, F, MF, EF, A] =
    withWhere(f(meta))

  final def whereAnd[A](
    f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]
  ): SelfT[T, F, MF, EF, W ~ A] =
    withWhere(where && f(meta))

  final def whereOr[AC, A](
    f: Meta.Aux[T, F, MF, EF] => BooleanAction[A]
  ): SelfT[T, F, MF, EF, W ~ A] =
    withWhere(where || f(meta))

  private[blaireau] def where: BooleanAction[W]

  private[blaireau] def meta: Meta.Aux[T, F, MF, EF]
}
