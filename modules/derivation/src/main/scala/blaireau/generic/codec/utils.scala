// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec

import cats.data.State
import _root_.shapeless.{::, HList}
import skunk.{Codec, Decoder}
import skunk.data.Type

import cats.syntax.all._

private[blaireau] object utils {
  def mergeHlistCodecs[H, T <: HList](hCodec: Codec[H], tCodec: Codec[T]): Codec[H :: T] = new Codec[H :: T] { self =>
    override val sql: State[Int, String] = (hCodec.sql, tCodec.sql).mapN((a, b) => s"$a, $b")
    override val types: List[Type]       = hCodec.types ++ tCodec.types

    override def encode(a: H :: T): List[Option[String]] = hCodec.encode(a.head) ++ tCodec.encode(a.tail)

    override def decode(offset: Int, ss: List[Option[String]]): Either[Decoder.Error, H :: T] = {
      val (sa, sb) = ss.splitAt(hCodec.types.length)
      (hCodec.decode(offset, sa), tCodec.decode(offset + hCodec.length, sb)).mapN(_ :: _)
    }
  }
}
