package blaireau.dsl

import skunk.Codec

object DSLSpec extends App {
  import blaireau.generic.codec.instances.all._
  import shapeless.record._
  import blaireau.dsl.syntax._

  final case class User(`type`: Float, name: String, age: Int, kaka: Double)
  val users = Table[User]("users")

  val allSophiesInTheir30s = users
    .select("type", "name")
    .where(t => t.name === "Sophie" && t.age >= 30 && t.age < 40)
    .toQuery

  println(allSophiesInTheir30s.sql)

  import skunk._
  val allUsers: Query[Void, User] = users.select.toQuery.gmap[User]
  println(allUsers.sql)

// shapeless.ops.hlist.Mapper.Aux[blaireau.dsl.metaFieldToCodec.type,
// blaireau.metas.MetaField[String] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("type")], blaireau.metas.MetaField[String]] ::
// blaireau.metas.MetaField[String] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("name")], blaireau.metas.MetaField[String]] ::
// blaireau.metas.MetaField[Int] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("age")],blaireau.metas.MetaField[Int]] ::
// shapeless.HNil,MO
// ]
//  val allUsers = users.select.toQuery
//  println(allUsers.sql)
}
