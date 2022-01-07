// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import blaireau.dsl.actions.TwiddlerInstances
import blaireau.dsl.assignment.MetaFieldAssignmentSyntax
import blaireau.dsl.filtering.MetaFieldBooleanSyntax
import blaireau.metas.instances.AllMetaInstances

package object dsl
  extends AllMetaInstances
  with MetaFieldBooleanSyntax
  with MetaFieldAssignmentSyntax
  with TwiddlerInstances {

  // TODO remove ? Helpers for intellij
  //  implicit def _dummyMapper[O <: HList]: Mapper.Aux[assignmentApplier.type, Nothing, O] =
  //    throw new IllegalStateException()
  //  implicit def _dummyHlistReducer[O <: HList]: LeftReducer.Aux[Nothing, actionAssignmentFolder.type, O] =
  //    throw new IllegalStateException()
  //  implicit def _dummyEq[A]: =:=[Nothing, AssignmentAction[A]] =
  //    throw new IllegalStateException()
  //  implicit def _dummyTw: Twiddler.Aux[Option[Nothing], Nothing] =
  //    throw new IllegalStateException()
}
