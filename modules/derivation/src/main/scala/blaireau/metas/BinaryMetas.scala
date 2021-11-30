// Written by Valentin HENRY
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.Meta
import skunk.codec.BinaryCodecs

trait BinaryMetas extends BinaryCodecs {
  implicit val byteArrayMeta: Meta[Array[Byte]] = Meta(bytea, Nil)
}
