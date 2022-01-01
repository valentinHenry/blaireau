// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import blaireau.dsl._
import blaireau.dsl.filtering.BooleanAction
import blaireau.metas.Meta
import munit.FunSuite
import shapeless.the
import skunk.implicits.toIdOps
import skunk.{Codec, ~}

class BooleanActionSpec extends FunSuite {
  case class Points(yes: Long, no: Int)

  val pointCodec: Codec[Points] = (int8 ~ int4).gimap[Points]

  case class Maybe(ok: Int, oui: Option[Long])

  val maybeCodec                     = (int4 ~ int8.opt).gimap[Maybe]
  val blaireauCodec: Codec[Blaireau] = (text ~ int4 ~ pointCodec ~ text.opt ~ maybeCodec.opt ~ bool).gimap[Blaireau]

  private[this] def assert[A](assign: BooleanAction[A], dummy: A, sql: String, codec: Codec[A]): Unit = {
    assertEquals(assign.toFragment.sql, sql)
    assertEquals(assign.elt, dummy)
    assertEquals(assign.codec.toString(), codec.toString())
  }

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
    assert(meta.something.isEmpty, skunk.Void, "something IS NULL", skunk.Void.codec)
  }

  test("single option isDefined") {
    assert(meta.something.isDefined, skunk.Void, "something IS NOT NULL", skunk.Void.codec)
  }

  test("single option exists") {
    assert(meta.something.exists(_ === "hmmm"), "hmmm", "(something IS NOT NULL AND something = $1)", text)
  }

  test("single option exists with boolean") {
    assert(
      meta.something.exists(_ === "hmmm" && meta.check),
      "hmmm" ~ skunk.Void,
      "(something IS NOT NULL AND (something = $1 AND check))",
      text ~ skunk.Void.codec
    )
  }

  test("single option forall") {
    assert(meta.something.forall(_ <> "ok"), "ok", "(something IS NULL OR something <> $1)", text)
  }

  test("single option contains") {
    assert(meta.something.contains("ok"), "ok", "something = $1", text)
  }

  test("embedded option isEmpty") {
    assert(meta.maybe.isEmpty, skunk.Void, "(NOT (ok IS NOT NULL OR oui IS NOT NULL))", skunk.Void.codec)
  }

  test("embedded option isDefined") {
    assert(meta.maybe.isDefined, skunk.Void, "(ok IS NOT NULL OR oui IS NOT NULL)", skunk.Void.codec)
  }

  test("embedded option exists") {
    val dummy: Maybe = Maybe(1, None)
    assert(
      meta.maybe.exists(_ === dummy),
      dummy,
      "((ok IS NOT NULL OR oui IS NOT NULL) AND (ok = $1 AND oui = $2))",
      maybeCodec
    )
  }

  test("embedded option forall") {
    val dummy: Maybe = Maybe(1, None)
    assert(
      meta.maybe.forall(_ <> dummy),
      dummy,
      "((NOT (ok IS NOT NULL OR oui IS NOT NULL)) OR (ok <> $1 OR oui <> $2))",
      maybeCodec
    )
  }

  test("embedded option contains") {
    val dummy: Maybe = Maybe(1, None)
    assert(meta.maybe.contains(dummy), dummy, "(ok = $1 AND oui = $2)", maybeCodec)
  }

  test("embedded option equal") {
    val dummy: Option[Maybe] = Some(Maybe(1, None))
    assert(meta.maybe === dummy, dummy, "(ok = $1 AND oui = $2)", maybeCodec.opt)
  }

  test("two operators &&") {
    assert(meta.name === "test" && meta.age <= 5, ("test", 5), "(name = $1 AND age <= $2)", text ~ int4)
  }

  test("two operators ||") {
    assert(meta.name === "test" || meta.age <= 5, ("test", 5), "(name = $1 OR age <= $2)", text ~ int4)
  }

  test("multiple operators parens") {
    val action: BooleanAction[String ~ Int ~ String] = (meta.name === "test" || meta.age <= 5) && meta.name =!= "tset"

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

  case class Blaireau(
    name: String,
    age: Int,
    points: Points,
    something: Option[String],
    maybe: Option[Maybe],
    check: Boolean
  )
}
