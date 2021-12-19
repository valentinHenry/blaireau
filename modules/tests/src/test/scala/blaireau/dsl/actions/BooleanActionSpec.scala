package blaireau.dsl.actions

import blaireau.dsl._
import blaireau.metas.Meta
import munit.FunSuite
import shapeless.the
import skunk.{Codec, ~}

class BooleanActionSpec extends FunSuite {
  case class Points(yes: Long, no: Int)
  val pointCodec: Codec[Points] = (int8 ~ int4).gimap[Points]

  case class Blaireau(name: String, age: Int, points: Points)
  val blaireauCodec: Codec[Blaireau] = (text ~ int4 ~ pointCodec).gimap[Blaireau]

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

  private[this] def assert[A](assign: BooleanAction[A], dummy: A, sql: String, codec: Codec[A]): Unit = {
    assertEquals(assign.toFragment.sql, sql)
    assertEquals(assign.elt, dummy)
    assertEquals(assign.codec.toString(), codec.toString())
  }
}
