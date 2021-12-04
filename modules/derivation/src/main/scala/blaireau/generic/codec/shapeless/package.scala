// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec

import _root_.shapeless.{::, Generic, HList, HNil, Lazy}
import skunk.{Codec, Void}

package object shapeless {
  implicit val hnilCodec: Codec[HNil] = Void.codec.imap(_ => ???)(_ => Void)

  implicit def hlistCodec[H, T <: HList](implicit hCodec: Lazy[Codec[H]], tCodec: Codec[T]): Codec[H :: T] =
    utils.mergeHlistCodecs(hCodec.value, tCodec)

  implicit final def genericCodecEncoder[A, H](implicit
    generic: Generic.Aux[A, H],
    hMeta: Lazy[Codec[H]]
  ): Codec[A] =
    hMeta.value.imap(generic.from)(generic.to)
}
