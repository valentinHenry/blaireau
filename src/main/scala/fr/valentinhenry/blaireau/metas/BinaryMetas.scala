package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.MetaType
import skunk.codec.BinaryCodecs

trait BinaryMetas extends BinaryCodecs {
  implicit val byteArrayMeta: MetaType[Array[Byte]] = MetaType(bytea)
}
