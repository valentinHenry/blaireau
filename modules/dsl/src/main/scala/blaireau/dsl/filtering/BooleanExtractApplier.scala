// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import shapeless.HList

class BooleanExtractApplier[T, EF <: HList](
  val fullEq: BooleanFullEqExtractApplier[T, EF],
  val semiEq: BooleanSemiEqExtractApplier[T, EF],
  val neq: BooleanNeqExtractApplier[T, EF],
  val fullNeq: BooleanFullNeqExtractApplier[T, EF]
) {
  def imap[B](f: T => B)(g: B => T): BooleanExtractApplier[B, EF] =
    new BooleanExtractApplier[B, EF](
      fullEq.imap(f)(g),
      semiEq.imap(f)(g),
      neq.imap(f)(g),
      fullNeq.imap(f)(g)
    )
}

object BooleanExtractApplier {
  implicit def instance[T, EF <: HList](implicit
    fullEq: BooleanFullEqExtractApplier[T, EF],
    semiEq: BooleanSemiEqExtractApplier[T, EF],
    neq: BooleanNeqExtractApplier[T, EF],
    fullNeq: BooleanFullNeqExtractApplier[T, EF]
  ): BooleanExtractApplier[T, EF] =
    new BooleanExtractApplier[T, EF](fullEq, semiEq, neq, fullNeq)
}
