package blaireau.dsl.utils

import cats.data.State
import cats.implicits.catsSyntaxEitherId
import skunk.util.Origin
import skunk.{Codec, Fragment, Void}

object FragmentUtils {
  def const(sql: String): Fragment[Void] =
    Fragment(
      parts = sql.asLeft[State[Int, String]] :: Nil,
      encoder = Void.codec,
      origin = Origin.unknown
    )

  def withValue[A](sql: String, codec: Codec[A]): Fragment[A] =
    Fragment(
      parts = sql.asLeft[State[Int, String]] :: codec.sql.asRight[String] :: Nil,
      encoder = codec,
      origin = Origin.unknown
    )
}
