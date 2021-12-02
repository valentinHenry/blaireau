// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.meta.instances

import blaireau.Meta
import skunk.codec.BinaryCodecs

trait BinaryMetaInstances extends BinaryCodecs {
  implicit val byteArrayMeta: Meta[Array[Byte]] = Meta(bytea, Nil)
}
