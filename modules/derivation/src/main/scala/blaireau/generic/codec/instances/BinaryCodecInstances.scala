// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec.instances

import skunk.Codec
import skunk.codec.BinaryCodecs

trait BinaryCodecInstances extends BinaryCodecs {
  implicit val byteArrayCodec: Codec[Array[Byte]] = bytea
}