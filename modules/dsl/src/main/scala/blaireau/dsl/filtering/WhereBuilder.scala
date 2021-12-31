package blaireau.dsl.filtering

import blaireau.metas.Meta
import shapeless.HList
import skunk.~

trait WhereBuilder[T, F <: HList, MF <: HList, EF <: HList, WC, W] {
  private[blaireau] def where: BooleanAction[WC, W]

  type SelfT[T0, F0 <: HList, MF0 <: HList, EF0 <: HList, WC0, W0]

  def withWhere[NWC, NW](newWhere: BooleanAction[NWC, NW]): SelfT[T, F, MF, EF, NWC, NW]

  final def where[AC, A](f: Meta.Aux[T, F, MF, EF] => BooleanAction[AC, A]): SelfT[T, F, MF, EF, AC, A] =
    withWhere(f(meta))

  final def whereAnd[AC, A](
    f: Meta.Aux[T, F, MF, EF] => BooleanAction[AC, A]
  ): SelfT[T, F, MF, EF, WC ~ AC, W ~ A] =
    withWhere(where && f(meta))

  final def whereOr[AC, A](
    f: Meta.Aux[T, F, MF, EF] => BooleanAction[AC, A]
  ): SelfT[T, F, MF, EF, WC ~ AC, W ~ A] =
    withWhere(where || f(meta))

  private[blaireau] def meta: Meta.Aux[T, F, MF, EF]
}
