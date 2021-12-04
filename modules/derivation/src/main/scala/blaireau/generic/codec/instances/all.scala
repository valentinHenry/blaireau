// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec.instances

import skunk.Codec

object all extends AllCodecInstances

trait AllCodecInstances
    extends CodecDerivation
    with NumericCodecInstances
    with BinaryCodecInstances
    with UuidCodecInstances
    with EnumCodecInstances
    with TextCodecInstances
    with BooleanCodecInstances
    with TemporalCodecInstances
    with VoidCodecInstances

trait CodecDerivation {
  implicit final def deriveOptionalCodecType[T](implicit m: Codec[T]): Codec[Option[T]] = m.opt
}

trait VoidCodecInstances {
  implicit final val voidCodec: Codec[skunk.Void] = skunk.Void.codec
}
