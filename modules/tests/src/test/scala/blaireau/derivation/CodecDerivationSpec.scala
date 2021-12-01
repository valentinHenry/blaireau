package blaireau.derivation

import munit.FunSuite
import skunk.Codec

class CodecDerivationSpec extends FunSuite {
  test("Can derive simple coded") {
    import blaireau.generic.codec.auto._

    case class Simple(test: String)

    val c: Codec[Simple] = implicitly

    assert(c != null)
  }

  test("Can derive nested codec") {
    import blaireau.generic.codec.auto._

    case class Simple(test: String)
    case class NotSimple(ok: String, simple: Simple)

    val c: Codec[NotSimple] = implicitly

    assert(c != null)
  }

  test("Can derive optional nested codec") {
    import blaireau.generic.codec.auto._

    case class Simple(test: String)
    case class NotSimple(ok: String, simple: Option[Simple])

    val c: Codec[NotSimple] = implicitly

    assert(c != null)
  }

  //FIXME make codec derivation only deriving codecs, not metas
//  test("Can semi-derive codec") {
//    import blaireau.generic.codec.semiauto._
//    import blaireau.metas.text._
//    import blaireau.metas.numeric._
//
//    case class Address(street: String, forest: String)
//
//    case class Blaireau(name: String, age: Int, address: Address)
//
//    implicit val addressCodec: Codec[Address] = (varchar ~ varchar(16)).gimap[Address]
//    val blaireauCodec: Codec[Blaireau]        = deriveCodec[Blaireau]
//
//    assert(blaireauCodec != null)
//  }
}
