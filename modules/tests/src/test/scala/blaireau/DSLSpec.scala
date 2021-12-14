package blaireau

import skunk.Query

import java.time.OffsetDateTime

object DSLSpec extends App {
  import blaireau.dsl._

  final case class Audit(cratedAt: OffsetDateTime, createdBy: String, updatedAt: OffsetDateTime, updatedBy: String)
  final case class Address(street: String, postalCode: String, city: String, country: String)
  final case class User(firstName: String, lastName: String, age: Int, address: Address, audit: Audit)

  val users = Table[User]("users")

  val allSophiesInTheir30s = users
    .select(e => e.firstName ~ e.lastName ~ e.address.street ~ e.audit)
    .where(t => t.firstName === "Sophie" && t.age >= 30 && t.age < 40 && t.address.city === "Manchester")

  println(users.meta.metaFields)
  println(allSophiesInTheir30s.toQuery.sql)
  println(allSophiesInTheir30s.queryIn)

  //could not find implicit value for parameter flatten: blaireau.utils.SelectFlatten.Aux[(
  // blaireau.metas.MetaField[String] ~
  // blaireau.metas.MetaField[String] ~
  // blaireau.metas.MetaField[String] ~
  // blaireau.metas.MetaElt[blaireau.DSLSpec.Audit]{type F = blaireau.metas.MetaField[java.time.OffsetDateTime] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("cratedAt")],blaireau.metas.MetaField[java.time.OffsetDateTime]] :: blaireau.metas.MetaField[String] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("createdBy")],blaireau.metas.MetaField[String]] :: blaireau.metas.MetaField[java.time.OffsetDateTime] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("updatedAt")],blaireau.metas.MetaField[java.time.OffsetDateTime]] :: blaireau.metas.MetaField[String] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("updatedBy")],blaireau.metas.MetaField[String]] :: shapeless.HNil}) :: shapeless.HNil,FS]

//  val allUsers: Query[skunk.Void, User] = users.select.toInstanceQuery
//  println(allUsers.sql)
//
// shapeless.HNil,MO
// Cannot prove that
//  val allUsers = users.select.toQuery
//  println(allUsers.sql)
}
