// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import munit.FunSuite
import blaireau.metas.Meta
import shapeless.the
import blaireau.dsl._
import blaireau.dsl.assignment.AssignmentAction
import skunk.{Codec, ~}
class AssignmentActionSpec extends FunSuite {

  case class Points(yes: Long, no: Int)
  val pointCodec: Codec[Points] = (int8 ~ int4).gimap[Points]

  case class Blaireau(name: String, age: Int, points: Points)
  val blaireauCodec: Codec[Blaireau] = (text ~ int4 ~ pointCodec).gimap[Blaireau]

  val meta = the[Meta[Blaireau]]

  test("One simple assignation") {
    assert(meta.name := "newName", "newName", "name = $1", text)
  }

  test("Multiple assignations") {
    val assign: AssignmentAction[String ~ Int] = (meta.name := "newName") <+> (meta.age := 42)
    assert(assign, ("newName", 42), "name = $1, age = $2", (text ~ int4))
  }

  test("One embedded assignation") {
    val dummy = Points(5L, 0)
    assert(meta.points := dummy, dummy, "yes = $1, no = $2", pointCodec)
  }

  test("Full assignation") {
    val dummy = Blaireau("test", 5, Points(5L, 4))
    assert(meta := dummy, dummy, "name = $1, age = $2, yes = $3, no = $4", blaireauCodec)
  }

  private[this] def assert[A](assign: AssignmentAction[A], dummy: A, sql: String, codec: Codec[A]): Unit = {
    assertEquals(assign.toFragment.sql, sql)
    assertEquals(assign.elt, dummy)
    assertEquals(assign.codec.toString(), codec.toString())
  }
}
