package blaireau

import skunk.{Query, ~}
import blaireau.dsl._

import java.time.OffsetDateTime

object DSLSpec extends App {

  final case class Audit(cratedAt: OffsetDateTime, createdBy: String, updatedAt: OffsetDateTime, updatedBy: String)
  final case class Address(street: String, postalCode: String, city: String, country: String)
  final case class Simple(i: Int, j: Long)
  final case class User(firstName: String, lastName: String, age: Int, address: Address, audit: Audit, simple: Simple)

  val users = Table[User]("users")

  val allSophiesInTheir30s = users
    .select(e => e.firstName ~ e.lastName ~ e.address.street ~ e.audit)
    .where(t => t.firstName === "Sophie" && t.age >= 30 && t.age < 40 && t.address.city === "Manchester")

  println(users.meta.metaFields)
  println(allSophiesInTheir30s.toQuery.sql)
  println(allSophiesInTheir30s.queryIn)

  val selectedQuery: Query[skunk.Void, String ~ String ~ Simple] = users
    .select(e => e.firstName ~ e.lastName ~ e.simple)
    .toQuery

  println(selectedQuery.sql)

  val allUsers: Query[skunk.Void, User] = users.select.toQuery
  println(allUsers.sql)

  val allUsersQ = users.select.toQuery
  println(allUsersQ.sql)
}
