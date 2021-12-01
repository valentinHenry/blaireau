package blaireau.dsl.select

import blaireau.dsl.Table
import shapeless.HList
import skunk._
import skunk.util.{Origin, Twiddler}

class SelectQueryBuilder[Entity, In <: HList](
  table: Table[Entity],
  where: List[Fragment[_]],
  params: HList
)(implicit twiddler: Twiddler[In]) {
//  def where[A](fragment: Fragment[A]) =

  def cursor(implicit or: Origin)                 = ???
  def stream(chunkSize: Int)(implicit or: Origin) = ???
  def option(implicit or: Origin)                 = ???
  def unique(implicit or: Origin)                 = ???
  def pipe(chunkSize: Int)(implicit or: Origin)   = ???
}
