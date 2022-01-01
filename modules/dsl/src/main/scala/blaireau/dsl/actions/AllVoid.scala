// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import skunk.Void

trait AllVoid[A] {
  def from(v: Void): A
}

object AllVoid {
  implicit def base: AllVoid[Void] = (v: Void) => v

  implicit def inductive[A](implicit
    ev: AllVoid[A]
  ): AllVoid[(A, Void)] =
    (v: Void) => (ev.from(v), Void)
}
