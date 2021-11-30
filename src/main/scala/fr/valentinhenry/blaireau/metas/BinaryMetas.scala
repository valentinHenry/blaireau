package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.Meta
import skunk.codec.BinaryCodecs

trait BinaryMetas extends BinaryCodecs {
  implicit val byteArrayMeta: Meta[Array[Byte]] = Meta(bytea, Nil)
}
