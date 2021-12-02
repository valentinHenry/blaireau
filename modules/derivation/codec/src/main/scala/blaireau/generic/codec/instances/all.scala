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
    with TextMetaInstances
    with BooleanCodecInstances
    with TemporalCodecInstances

trait CodecDerivation {
  implicit final def deriveOptionalCodecType[T](implicit m: Codec[T]): Codec[Option[T]] = m.opt
}
