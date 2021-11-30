import fr.valentinhenry.blaireau.Meta

object Test extends App {
  import fr.valentinhenry.blaireau.generic.metas.auto._

  case class Address(road: String, postalCode: Option[Int])
  case class User(firstName: String, lastName: String)
  case class Info(secondaryAddress: Option[Address], idk: String)
  case class UserInfo(user: User, info: Option[Info], address: Option[Address] = None)

  val meta: Meta[UserInfo] = implicitly
  val address = Address("Test road", Some(33000))
  val user = User("test user", "lastname")
  val info = Info(None, "ok")
  val userInfo = UserInfo(user, Some(info))

  println(meta.encode(userInfo))
}
