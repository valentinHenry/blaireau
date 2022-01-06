// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.dsl._
import blaireau.dsl.selection.SelectableMeta
import blaireau.metas.{FieldProduct, Meta}
import munit.FunSuite
import shapeless.the
import skunk.codec.all._
import skunk.{Codec, ~}

class FieldProductSpec extends FunSuite {
  val pointCodec: Codec[Points]      = (int8 ~ int4).gimap[Points]
  val blaireauCodec: Codec[Blaireau] = (text ~ int4 ~ pointCodec).gimap[Blaireau]
  val meta                           = SelectableMeta.make(the[Meta[Blaireau]])

  case class Points(yes: Long, no: Int)

  case class Blaireau(name: String, age: Int, points: Points)

  test("select all") {
    val s: FieldProduct[Blaireau, _] = meta

    assertEquals(s.codec.toString(), blaireauCodec.toString())
  }

  test("select field") {
    val s: FieldProduct[String, _] = meta.name

    assertEquals(s.codec.toString(), text.toString())
  }

  test("select embedded class") {
    val s: FieldProduct[Points, _] = meta.points

    assertEquals(s.codec.toString(), pointCodec.toString())
  }

  test("select multiple fields") {
    val s: FieldProduct[String ~ Int, _] = meta.name ~ meta.age

    assertEquals(s.codec.toString, (text ~ int4).toString())
  }

  test("select multiple fields with embedded") {
    val s: FieldProduct[String ~ Points, _] = meta.name ~ meta.points

    assertEquals(s.codec.toString, (text ~ pointCodec).toString())
  }

  test("select embedded field") {
    val s: FieldProduct[Long, _] = meta.points.yes

    assertEquals(s.codec.toString, int8.toString())
  }
}
