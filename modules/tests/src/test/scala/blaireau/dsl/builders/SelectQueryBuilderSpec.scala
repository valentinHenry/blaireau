// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl._
import munit.FunSuite
import skunk.{Query, ~}

class SelectQueryBuilderSpec extends FunSuite {
  case class Blaireau(name: String, age: Int)
  val blaireaux = Table[Blaireau]("blaireaux")

  test("select all") {
    val s                                  = blaireaux.select
    val query: Query[skunk.Void, Blaireau] = s.toQuery

    assertEquals(s.queryIn, skunk.Void)
    assertEquals(query.sql, "SELECT name,age FROM blaireaux WHERE TRUE")
  }

  test("select one field") {
    val s                                = blaireaux.select(_.name)
    val query: Query[skunk.Void, String] = s.toQuery

    assertEquals(s.queryIn, skunk.Void)
    assertEquals(query.sql, "SELECT name FROM blaireaux WHERE TRUE")
  }

  test("select multiple fields") {
    val s                                      = blaireaux.select(e => e.name ~ e.age)
    val query: Query[skunk.Void, String ~ Int] = s.toQuery

    assertEquals(s.queryIn, skunk.Void)
    assertEquals(query.sql, "SELECT name,age FROM blaireaux WHERE TRUE")
  }

  test("select all with where") {
    val s                              = blaireaux.select.where(_.name === "Christophe")
    val query: Query[String, Blaireau] = s.toQuery

    assertEquals(s.queryIn, "Christophe")
    assertEquals(query.sql, "SELECT name,age FROM blaireaux WHERE name = $1")
  }

  test("select all with multiple where") {
    val s                                    = blaireaux.select.where(e => e.name === "Christophe" && e.age > 5)
    val query: Query[String ~ Int, Blaireau] = s.toQuery

    assertEquals(s.queryIn, ("Christophe", 5))
    assertEquals(query.sql, "SELECT name,age FROM blaireaux WHERE (name = $1 AND age > $2)")
  }

  test("select some with where") {
    val s                         = blaireaux.select(_.name).where(_.age >= 10)
    val query: Query[Int, String] = s.toQuery

    assertEquals(s.queryIn, 10)
    assertEquals(query.sql, "SELECT name FROM blaireaux WHERE age >= $1")
  }
}
