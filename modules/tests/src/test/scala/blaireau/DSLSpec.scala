package blaireau

import java.time.OffsetDateTime

object DSLSpec extends App {
  import blaireau._
  import blaireau.dsl._

  final case class Audit(cratedAt: OffsetDateTime, createdBy: String, updatedAt: OffsetDateTime, updatedBy: String)
  final case class Address(street: String, postalCode: String, city: String, country: String)
  final case class User(firstName: String, lastName: String, age: Int, address: Address, audit: Audit)

  val users = Table[User]("users")

  val allSophiesInTheir30s = users
    .select("firstName", "lastName", "age")
    .where(t => t.firstName === "Sophie" && t.age >= 30 && t.age < 40)

  println(users.meta.metaFields)
  println(allSophiesInTheir30s.toQuery.sql)
  println(allSophiesInTheir30s.queryIn)

  import skunk._
  val allUsers: Query[Void, User] = users.select.toInstanceQuery
  println(allUsers.sql)

// shapeless.ops.hlist.Mapper.Aux[blaireau.metaFieldToCodec.type,
// blaireau.metas.MetaField[String] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("type")], blaireau.metas.MetaField[String]] ::
// blaireau.metas.MetaField[String] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("name")], blaireau.metas.MetaField[String]] ::
// blaireau.metas.MetaField[Int] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("age")],blaireau.metas.MetaField[Int]] ::
// shapeless.HNil,MO
// ]
//  val allUsers = users.select.toQuery
//  println(allUsers.sql)
}
