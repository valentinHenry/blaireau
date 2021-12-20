// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import munit.FunSuite
import blaireau.dsl._
import skunk.Command

class DeleteCommandBuilderSpec extends FunSuite {
  case class Blaireau(name: String, age: Int)
  val blaireaux = Table[Blaireau]("blaireaux")

  test("delete element with specified value") {
    val c = blaireaux.delete.where(_.name === "Simon")

    val command: Command[String] = c.toCommand
    val commandIn: String        = c.commandIn
    assertEquals(command.sql, "DELETE FROM blaireaux WHERE name = $1")
    assertEquals(commandIn, "Simon")
//    assertEquals(command.encoder.toString(), text.asEncoder.toString()) TODO: Find a better way to check for equality between codecs
  }
}
