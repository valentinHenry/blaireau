import fr.valentinhenry.blaireau.MetaCodec

object Test extends App {
  import fr.valentinhenry.blaireau.generic.auto._

  case class Address(road: String, postalCode: Int)
  case class User(firstName: String, lastName: String, address: Address)

  val addressCodec: MetaCodec[Address] = implicitly
  println(addressCodec)
  val userCodec: MetaCodec[User] = implicitly
  println(userCodec)
}
