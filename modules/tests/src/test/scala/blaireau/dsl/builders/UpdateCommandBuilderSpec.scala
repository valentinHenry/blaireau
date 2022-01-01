// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl._
import munit.FunSuite
import skunk.{Command, ~}

class UpdateCommandBuilderSpec extends FunSuite {
  case class Blaireau(name: String, age: Int)
  val blaireaux = Table[Blaireau]("blaireaux")

  test("update whole object") {
    val dummy                               = Blaireau("Robert", 5)
    val c                                   = blaireaux.update(dummy).where(_.name === "Robert")
    val command: Command[Blaireau ~ String] = c.toCommand

    assertEquals(c.commandIn, (dummy, "Robert"))
    assertEquals(command.sql, "UPDATE blaireaux SET name = $1, age = $2 WHERE name = $3")
  }

  test("update one field") {
    val c                              = blaireaux.update(_.age := 5).where(_.name === "Robert")
    val command: Command[Int ~ String] = c.toCommand

    assertEquals(c.commandIn, (5, "Robert"))
    assertEquals(command.sql, "UPDATE blaireaux SET age = $1 WHERE name = $2")
  }
}
