package blaireau.dsl

import blaireau.dsl.actions.BooleanAction
import blaireau.metas.Meta
import shapeless.HList

class UpdateQueryBuilder[T, F <: HList, MF <: HList, U <: HList, W](
  tableName: String,
  meta: Meta.Aux[T, F, MF],
  updatedFields: U,
  where: BooleanAction[W]
) {}
