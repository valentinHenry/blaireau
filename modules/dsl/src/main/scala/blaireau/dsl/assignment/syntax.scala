// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.assignment

import blaireau.dsl.assignment.AssignmentAction._
import blaireau.metas.{Meta, MetaField, MetaUtils}
import shapeless.HList
import shapeless.ops.hlist.{LeftReducer, Mapper}
import skunk.util.Twiddler

import scala.annotation.unused

trait MetaFieldAssignmentSyntax {

  import MetaFieldAssignmentOps._

  implicit final def assignentOpsMetaSyntax[T, F <: HList, MF <: HList, EF <: HList](
    me: Meta.Aux[T, F, MF, EF]
  ): MetadOps[T, F, MF, EF] =
    new MetadOps[T, F, MF, EF](me)

  implicit final def assignmentOpsMetaFieldSyntax[T](mf: MetaField[T]): MetaFieldOps[T] =
    new MetaFieldOps[T](mf)

  implicit final def numericAssignmentOpsMetaFieldSyntax[T](mf: MetaField[T])(implicit
    @unused ev: Numeric[T]
  ): MetaFieldNumericOps[T] =
    new MetaFieldNumericOps[T](mf: MetaField[T])
}

object MetaFieldAssignmentOps {
  final class MetadOps[T, F <: HList, MF <: HList, EF <: HList](meta: Meta.Aux[T, F, MF, EF]) {
    def :=[MEF <: HList, LRO, UF](right: T)(implicit
      m: Mapper.Aux[assignmentApplier.type, EF, MEF],
      r: LeftReducer.Aux[MEF, actionAssignmentFolder.type, LRO],
      ev: LRO =:= AssignmentAction[UF],
      tw: Twiddler.Aux[T, UF]
    ): AssignmentAction[T] =
      MetaUtils
        .applyExtract[assignmentApplier.type, actionAssignmentFolder.type, T, EF, MEF, LRO, UF, AssignmentAction](
          right,
          meta.extract
        )
  }

  final class MetaFieldOps[T](mf: MetaField[T]) {
    def :=(right: T): AssignmentOp[T] =
      AssignmentOp[T](mf, right)
  }

  final class MetaFieldNumericOps[T](mf: MetaField[T]) {
    def +=(right: T): AssignmentIncr[T] =
      AssignmentIncr[T](mf, right)

    def -=(right: T): AssignmentDecr[T] =
      AssignmentDecr[T](mf, right)
  }
}
