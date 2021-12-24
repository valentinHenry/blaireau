// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.dsl._
import blaireau.dsl.filtering.{BooleanAction, IdBooleanAction}
import blaireau.metas.Meta
import munit.FunSuite
import shapeless.the
import skunk.{Codec, ~}

class BooleanActionSpec extends FunSuite {
  case class Points(yes: Long, no: Int)
  val pointCodec: Codec[Points] = (int8 ~ int4).gimap[Points]

  case class Something(well: Float)
  val somethingCodec: Codec[Something] = float4.gimap[Something]

  case class Blaireau(name: String, something: Option[Something], age: Int, points: Points, maybe: Option[String])
  val blaireauCodec: Codec[Blaireau] = (text ~ somethingCodec.opt ~ int4 ~ pointCodec ~ text.opt).gimap[Blaireau]

  val meta = the[Meta[Blaireau]]

  test("single equal(=)") {
    assert(meta.name === "test", "test", "name = $1", text)
  }

  test("single equal(=~=)") {
    assert(meta.name =~= "test", "test", "name = $1", text)
  }

  test("single not equal(<>)") {
    assert(meta.name <> "test", "test", "name <> $1", text)
  }

  test("single not equal(=!=)") {
    assert(meta.name =!= "test", "test", "name <> $1", text)
  }

  test("single like") {
    assert(meta.name.like("test"), "test", "name like $1", text)
  }

  test("single >") {
    assert(meta.age > 5, 5, "age > $1", int4)
  }

  test("single <") {
    assert(meta.age < 5, 5, "age < $1", int4)
  }

  test("single >=") {
    assert(meta.age >= 5, 5, "age >= $1", int4)
  }

  test("single <=") {
    assert(meta.age <= 5, 5, "age <= $1", int4)
  }

  test("single option isEmpty") {
    assert(meta.maybe.isEmpty, skunk.Void, "maybe IS NULL", skunk.Void.codec)
  }

  test("single option isDefined") {
    assert(meta.maybe.isDefined, skunk.Void, "maybe IS NOT NULL", skunk.Void.codec)
  }

  test("single option exists") {
    assert(meta.maybe.exists(_ === "hmmm"), "hmmm", "(maybe IS NOT NULL AND maybe = $1)", text)
  }

  test("single option forall") {
    assert(meta.maybe.forall(_ <> "ok"), "ok", "(maybe IS NULL OR maybe <> $1)", text)
  }

//  test("embedded option isEmpty") {
//    assert(meta.something.isEmpty, skunk.Void, "well IS NULL", skunk.Void.codec)
//  }
//
//  test("embedded option isDefined") {
//    assert(meta.something.isDefined, skunk.Void, "well IS NOT NULL", skunk.Void.codec)
//  }

  test("single option contains") {
    assert(meta.maybe.contains("Test"), "Test", "(maybe IS NOT NULL AND maybe = $1)", text.opt)
  }

  test("two operators &&") {
    assert(meta.name === "test" && meta.age <= 5, ("test", 5), "(name = $1 AND age <= $2)", text ~ int4)
  }

  test("two operators ||") {
    assert(meta.name === "test" || meta.age <= 5, ("test", 5), "(name = $1 OR age <= $2)", text ~ int4)
  }

  test("multiple operators parens") {
    val action: IdBooleanAction[String ~ Int ~ String] = (meta.name === "test" || meta.age <= 5) && meta.name =!= "tset"

    assert(
      action,
      (("test", 5), "tset"),
      "((name = $1 OR age <= $2) AND name <> $3)",
      text ~ int4 ~ text
    )
  }

  test("multiple operators parens") {
    assert(
      meta.name === "test" || (meta.age <= 5 && meta.name =!= "tset"),
      ("test", (5, "tset")),
      "(name = $1 OR (age <= $2 AND name <> $3))",
      text ~ (int4 ~ text)
    )
  }

  test("embedded equal(===)") {
    val dummy = Points(5L, 4)
    val eq    = meta.points === dummy
    assert(
      eq,
      dummy,
      "(yes = $1 AND no = $2)",
      pointCodec
    )
  }

  test("embedded equal(=~=)") {
    val dummy = Points(5L, 4)
    val eq    = meta.points =~= dummy
    assert(
      eq,
      dummy,
      "(yes = $1 OR no = $2)",
      pointCodec
    )
  }

  test("embedded not equal(<>)") {
    val dummy = Points(5L, 4)
    val eq    = meta.points <> dummy
    assert(
      eq,
      dummy,
      "(yes <> $1 OR no <> $2)",
      pointCodec
    )
  }

  test("embedded not equal(=!=)") {
    val dummy = Points(5L, 4)
    val eq    = meta.points =!= dummy
    assert(
      eq,
      dummy,
      "(yes <> $1 AND no <> $2)",
      pointCodec
    )
  }

  private[this] def assert[CA, A](assign: BooleanAction[CA, A], dummy: A, sql: String, codec: Codec[CA]): Unit = {
    assertEquals(assign.toFragment.sql, sql)
    assertEquals(assign.elt, dummy)
    assertEquals(assign.codec.toString(), codec.toString())
  }
}
