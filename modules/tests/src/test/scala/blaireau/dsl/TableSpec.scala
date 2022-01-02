// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl

import blaireau.{Configuration, FieldFormatter}
import munit.FunSuite

class TableSpec extends FunSuite {
  implicit val c: Configuration = Configuration(fieldFormatter = FieldFormatter.ALLCAPS)

  case class Maybe(ok: Int, oui: Option[Long])

  case class Points(yes: Long, no: Int)

  case class Blaireau(name: String, age: Int, points: Points, something: Option[String], maybe: Option[Maybe])

  val blaireaux = Table[Blaireau](name = "blaireaux")
    .columns(_.name -> "le_name", _.maybe.oui -> "maybe_yes")

  test("overriden fields are taken into account") {
    val query = blaireaux.select(e => e.name ~ e.maybe).where(_.name === "ok").whereAnd(_.maybe === None).toQuery.sql

    assertEquals(
      query,
      "SELECT le_name,OK,maybe_yes FROM blaireaux WHERE (le_name = $1 AND (OK = $2 AND maybe_yes = $3))"
    )
  }
}
