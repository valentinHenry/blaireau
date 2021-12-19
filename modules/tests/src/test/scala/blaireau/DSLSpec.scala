package blaireau

import blaireau.dsl._
import skunk.implicits.toIdOps
import skunk.{Command, Query, ~}

import java.time.OffsetDateTime
import java.util.UUID

object DSLSpec extends App {

  final case class Audit(createdAt: OffsetDateTime, createdBy: String, updatedAt: OffsetDateTime, updatedBy: String)
  final case class Address(street: String, postalCode: String, city: String, country: String)
  final case class Simple(i: Int, j: Long)
  final case class User(
    id: UUID,
    firstName: String,
    lastName: String,
    age: Int,
    address: Address,
    audit: Audit,
    simple: Simple
  )

  val users = Table[User]("users")

  val u =
    User(
      UUID.randomUUID(),
      "FN",
      "LN",
      0,
      Address("STR", "PC", "CT", "C"),
      Audit(OffsetDateTime.now(), "Me", OffsetDateTime.now(), "Me"),
      Simple(1, 2L)
    )

  val addr = Address("Grande rue", "33000", "Bordeaux", "France")
  val allPeopleLivingInThisAddress = users.select
    .where(e => e.address === addr)

  println(allPeopleLivingInThisAddress.toQuery.sql)
  println(allPeopleLivingInThisAddress.queryIn)

  val allPeopleNotLivingInThisAddress = users.select
    .where(e => e.address <> addr)

  println(allPeopleNotLivingInThisAddress.toQuery.sql)
  print(allPeopleNotLivingInThisAddress.queryIn)

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

  val changeSophiesNameToSophia = users
    .update(e => (e.firstName := "Sophia") <+> (e.age += 6))
    .where(_.firstName === "Sophie")
    .whereAnd(_.lastName <> "Dupond")

  println(changeSophiesNameToSophia.toCommand.sql)
  println(changeSophiesNameToSophia.commandIn)

  val updateFullUser = users.update(u).where(_.id === u.id)
  println(updateFullUser.toCommand.sql)
  println(updateFullUser.commandIn)

  val updateUserAddress = users
    .update(_.address := Address("Grande rue", "33000", "Bordeaux", "France"))
    .where(_.id === u.id)

  println(updateUserAddress.toCommand.sql)
  println(updateUserAddress.commandIn)

  case class Test(one: Int, two: String, three: Long)

  val tests                           = Table[Test]("tests")
  val t                               = Test(1, "two", 3L)
  val updateTest: Command[Test ~ Int] = tests.update(t).where(_.one === 1).toCommand

  def updateUserAddressAndAgeCommand(id: UUID, address: Address, age: Int): Command[Address ~ Int ~ UUID] =
    users.update(u => (u.address := address) <+> (u.age := age)).where(_.id === id).toCommand

  val in: String ~ (String ~ String) = users
    .update(_.address.street := "Teerts Street")
    .where(e => e.firstName === "Chloe" && e.lastName === "Fontvi")
    .commandIn

  println(in)

  println(updateUserAddressAndAgeCommand(UUID.randomUUID(), addr, 15).sql)

  val deleteAllUsersWhoAreNamedMonique = users.delete.where(_.firstName === "Monique")
  println(deleteAllUsersWhoAreNamedMonique.toCommand.sql)
  println(deleteAllUsersWhoAreNamedMonique.commandIn)

  val insertUser = users.insert.value(u)
  println(insertUser.toCommand.sql)
  println(insertUser.commandIn)

  val insertUsers = users.insert.values(List(u, u.copy(id = UUID.randomUUID())))
  println(insertUsers.toCommand.sql)
  println(insertUsers.commandIn)

  val insertIdAndAddress = users.insert(u => u.id ~ u.address).value(u.id ~ u.address)
  println(insertIdAndAddress.toCommand.sql)
  println(insertIdAndAddress.commandIn)
}
