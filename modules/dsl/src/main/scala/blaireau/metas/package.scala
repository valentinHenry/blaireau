// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import shapeless.{HList, HNil}

package object metas {
  type MetaS[T] = Meta.Aux[T, HNil, HNil, HNil, HNil]

  type ExtractedField[T]                                                    = (MetaField[T], T)
  type ExtractedOptionalField[T]                                            = (OptionalMetaField[T], Option[T])
  type ExtractedMeta[T, F <: HList, MF <: HList, EF <: HList, NEF <: HList] = (Meta.Aux[T, F, MF, EF, NEF], T)
  type ExtractedOptionalMeta[T, MF <: HList, EF <: HList, IF <: HList, IMF <: HList, IEF <: HList] =
    (OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF], Option[T])
}
