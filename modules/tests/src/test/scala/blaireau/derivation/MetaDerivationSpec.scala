package blaireau.derivation

import blaireau.Meta
import blaireau.generic.meta.BlaireauConfiguration
import munit.FunSuite

class MetaDerivationSpec extends FunSuite {

  test("Can derive simple coded") {
    import blaireau.generic.meta.auto._

    implicit val config: BlaireauConfiguration = BlaireauConfiguration.default

    case class Simple(test: String)

    val c: Meta[Simple] = implicitly

    assertEquals(c.fields.map(_.name), List("test"))
    assertEquals(c.codec.types.map(_.name), List("text"))
  }

  test("Can derive nested codec") {
    import blaireau.generic.meta.auto._

    implicit val config: BlaireauConfiguration = BlaireauConfiguration.default

    case class Simple(test: String)
    case class NotSimple(ok: String, simple: Simple)

    val c: Meta[NotSimple] = implicitly

    assertEquals(c.fields.map(_.name), List("ok", "test"))
    assertEquals(c.codec.types.map(_.name), List("text", "text"))
  }

  test("Can derive optional nested codec") {
    import blaireau.generic.meta.auto._

    implicit val config: BlaireauConfiguration = BlaireauConfiguration.default

    case class Simple(test: String, test2: Long)
    case class NotSimple(ok: String, simple: Option[Simple], really: Double)

    val c: Meta[NotSimple] = implicitly

    assertEquals(c.fields.map(_.name), List("ok", "test", "test2", "really"))
    assertEquals(c.codec.types.map(_.name), List("text", "text", "int8", "float8"))
  }

  test("Name from CamelCase to snake_case") {
    import blaireau.generic.meta.auto._

    implicit val config: BlaireauConfiguration = BlaireauConfiguration.default

    case class Test(ohOkay: String, howAreYou: Option[String], fine: String)

    val c: Meta[Test] = implicitly

    assertEquals(c.fields.map(_.sqlName), List("oh_okay", "how_are_you", "fine"))
  }
}
