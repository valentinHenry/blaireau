// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.metas.ExtractApplier
import shapeless.HList

package object filtering {
  type BooleanFullEqExtractApplier[T, EF <: HList] = // ===
    ExtractApplier[booleanEqAndApplier.type, actionBooleanAndFolder.type, T, EF, BooleanAction]

  type BooleanSemiEqExtractApplier[T, EF <: HList] = // =~=
    ExtractApplier[booleanEqOrApplier.type, actionBooleanOrFolder.type, T, EF, BooleanAction]

  type BooleanNeqExtractApplier[T, EF <: HList] = // <>
    ExtractApplier[booleanNEqOrApplier.type, actionBooleanOrFolder.type, T, EF, BooleanAction]

  type BooleanFullNeqExtractApplier[T, EF <: HList] = // =!=
    ExtractApplier[booleanNEqAndApplier.type, actionBooleanAndFolder.type, T, EF, BooleanAction]
}
