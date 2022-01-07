// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.metas.ExtractApplier
import shapeless.HList

package object assignment {
  type AssignationExtractApplier[T, EF <: HList] =
    ExtractApplier[assignmentApplier.type, actionAssignmentFolder.type, T, EF, AssignmentAction]
}
