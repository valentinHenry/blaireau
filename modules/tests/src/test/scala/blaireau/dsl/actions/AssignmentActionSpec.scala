// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.dsl._
import blaireau.dsl.assignment.{AssignableMeta, AssignmentAction}
import blaireau.metas.Meta
import munit.FunSuite
import shapeless.the
import skunk.codec.all._
import skunk.{Codec, ~}

class AssignmentActionSpec extends FunSuite {

  val pointCodec: Codec[Points] = (int8 ~ int4).gimap[Points]
  val maybeCodec                = (int4 ~ int8.opt).gimap[Maybe]
  val meta                      = AssignableMeta.makeSelectable(the[Meta[Blaireau]])

  private[this] def assert[A](assign: AssignmentAction[A], dummy: A, sql: String, codec: Codec[A]): Unit = {
    assertEquals(assign.toFragment(FieldNamePicker.empty).sql, sql)
    assertEquals(assign.elt, dummy)
    assertEquals(assign.codec.toString(), codec.toString())
  }

  case class Points(yes: Long, no: Int)

  val blaireauCodec: Codec[Blaireau] = (text ~ int4 ~ pointCodec ~ text.opt ~ maybeCodec.opt).gimap[Blaireau]

  case class Maybe(ok: Int, oui: Option[Long])

  test("One simple assignation") {
    assert(meta.name := "newName", "newName", "name = $1", text)
  }

  test("Multiple assignations") {
    val assign: AssignmentAction[String ~ Int] = (meta.name := "newName") <+> (meta.age := 42)
    assert(assign, ("newName", 42), "name = $1, age = $2", text ~ int4)
  }

  test("One embedded assignation") {
    val dummy = Points(5L, 0)
    assert(meta.points := dummy, dummy, "yes = $1, no = $2", pointCodec)
  }

  test("One embedded optional assignation") {
    assert(meta.maybe := None, None, "ok = $1, oui = $2", maybeCodec.opt)
  }

  test("Embedded object field assignation") {
    assert(meta.points.yes := 5L, 5L, "yes = $1", int8)
  }

  test("Full assignation") {
    val dummy = Blaireau("test", 5, Points(5L, 4), None, None)
    assert(
      meta := dummy,
      dummy,
      "name = $1, age = $2, yes = $3, no = $4, something = $5, ok = $6, oui = $7",
      blaireauCodec
    )
  }

  case class Blaireau(name: String, age: Int, points: Points, something: Option[String], maybe: Option[Maybe])
}
