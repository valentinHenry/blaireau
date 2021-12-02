package blaireau.dsl

import blaireau.dsl.table.Table
import blaireau.generic.meta.BlaireauConfiguration
import blaireau.generic.meta.auto._

object DSLSpec extends App {
  implicit val config: BlaireauConfiguration = BlaireauConfiguration.default

  case class Address(address: String, city: String, code: String, country: String)

  case class Blaireau(name: String, age: Int, address: Address)

  val blaireaux: Table[Blaireau] = Table[Blaireau]("blaireaux")
}
