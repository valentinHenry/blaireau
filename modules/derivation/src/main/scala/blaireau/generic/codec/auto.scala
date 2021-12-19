// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec

import _root_.shapeless.{::, Generic, HList, HNil, Lazy}
import shapeless.ops.hlist.{Init, Last, Prepend}
import skunk.util.Twiddler
import skunk.{Codec, ~}

import scala.annotation.nowarn

object auto extends ShapelessDerivation

trait ShapelessDerivation {
  implicit final def genericCodecEncoder[A, H, CT](implicit
    generic: Generic.Aux[A, H],
    codec0: Lazy[Codec0.Aux[H, CT]],
    tw: Twiddler.Aux[H, CT]
  ): Codec[A] =
    codec0.value.codec.gimap
}

trait Codec0[T] {
  type CodecT
  def codec: Codec[CodecT]
}

object Codec0 {
  type Aux[T, C] = Codec0[T] { type CodecT = C }

  def apply[T, C](c: Codec[C]): Codec0.Aux[T, C] = new Codec0[T] {
    override type CodecT = C
    override val codec: Codec[CodecT] = c
  }

  implicit def base0[H](implicit
    hCodec: Lazy[Codec[H]]
  ): Codec0.Aux[H :: HNil, H] =
    Codec0(hCodec.value)

  @nowarn
  implicit def hlistCodec0[
    A <: HList,
    LO,
    IO <: HList,
    CO
  ](implicit
    init: Init.Aux[A, IO],
    last: Last.Aux[A, LO],
    hCodec: Codec[LO],
    tCodec: Codec0.Aux[IO, CO],
    a: Prepend.Aux[IO, LO :: HNil, A]
  ): Codec0.Aux[A, CO ~ LO] =
    Codec0(tCodec.codec ~ hCodec)
}
