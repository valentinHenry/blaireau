// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances.newtype

import blaireau.metas.MetaS
import blaireau.metas.instances.all._
import blaireau.metas.instances.newtype.objects.TypeTest
import io.estatico.newtype.macros.newtype
import munit.FunSuite

class NewtypeMetaInstanceSpec extends FunSuite {

  test("derive newtype meta") {
    val nesMeta: MetaS[TypeTest] = implicitly

    assert(nesMeta != null)
  }
}

object objects {
  @newtype case class TypeTest(i: Int)
}
