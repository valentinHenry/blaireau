// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances.refined

import blaireau.metas.MetaS
import blaireau.metas.instances.all._
import eu.timepit.refined.types.all.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import munit.FunSuite

class RefinedMetaInstancesSpec extends FunSuite {
  test("non empty string") {
    val nesMeta: MetaS[NonEmptyString] = implicitly

    assert(nesMeta != null)
  }

  test("posint") {
    val posintMeta: MetaS[PosInt] = implicitly

    assert(posintMeta != null)
  }
}
