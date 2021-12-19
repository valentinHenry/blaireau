// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl._
import munit.FunSuite
import skunk.Command

class InsertCommandBuilderSpec extends FunSuite {
  case class Blaireau(name: String, age: Int)
  val blaireaux = Table[Blaireau]("blaireaux")

  test("insert single value") {
    val jean                       = Blaireau("Jean", 5)
    val c                          = blaireaux.insert.value(jean)
    val command: Command[Blaireau] = c.toCommand

    assertEquals(c.commandIn, jean)
    assertEquals(command.sql, "INSERT INTO blaireaux(name,age) VALUES ($1, $2)")
  }

  test("insert multiple values") {
    val jean                             = Blaireau("Jean", 5)
    val herald                           = Blaireau("Herald", 3)
    val inserted                         = jean :: herald :: Nil
    val c                                = blaireaux.insert.values(inserted)
    val command: Command[List[Blaireau]] = c.toCommand

    assertEquals(c.commandIn, inserted)
    assertEquals(command.sql, "INSERT INTO blaireaux(name,age) VALUES ($1, $2), ($3, $4)")
  }

  test("insert single specific value") {
    val c                        = blaireaux.insert(_.name).value("Jean")
    val command: Command[String] = c.toCommand

    assertEquals(c.commandIn, "Jean")
    assertEquals(command.sql, "INSERT INTO blaireaux(name) VALUES ($1)")
  }

  test("insert multiple specific values") {
    val names                          = "Jean" :: "Herald" :: Nil
    val c                              = blaireaux.insert(_.name).values(names)
    val command: Command[List[String]] = c.toCommand

    assertEquals(c.commandIn, names)
    assertEquals(command.sql, "INSERT INTO blaireaux(name) VALUES ($1), ($2)")
  }
}
