package blaireau.dsl

import blaireau.metas.{Meta, MetaField}
import shapeless.HList

class SelectQueryBuilder[T, MT, MF <: HList, S <: HList](name: String, meta: Meta.Aux[MT, MF])(
  val select: S
) {}
