// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.derivation

import munit.FunSuite
import skunk.Codec

class CodecDerivationSpec extends FunSuite {
  test("Can derive simple coded") {
    import blaireau.generic._

    case class Simple(test: String)

    val c: Codec[Simple] = implicitly

    assertEquals(c.types.map(_.toString()), List("text"))
  }

  test("Can derive nested codec") {
    import blaireau.generic._

    case class Simple(test: String)
    case class NotSimple(ok: String, simple: Simple)

    val c: Codec[NotSimple] = implicitly

    assertEquals(c.types.map(_.toString()), List("text", "text"))
  }

  test("Can derive optional nested codec") {
    import blaireau.generic._

    case class Simple(test: String)
    case class NotSimple(ok: String, simple: Option[Simple])

    val c: Codec[NotSimple] = implicitly

    assertEquals(c.types.map(_.toString()), List("text", "text"))
  }

  test("Can derive nested codec with other types") {
    import blaireau.generic._

    case class Simple(test: String)
    implicit val simpleCodec: Codec[Simple] = varchar(16).gimap

    case class NotSimple(ok: String, simple: Option[Simple])

    val c: Codec[NotSimple] = implicitly

    assertEquals(c.types.map(_.toString()), List("text", "varchar(16)"))
  }
}
