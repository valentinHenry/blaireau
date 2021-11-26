package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.MetaType
import skunk.Codec

object all extends AllMetas

trait AllMetas
    extends MetaCodecDerivation
    with NumericMetas
    with BinaryMetas
    with UuidMetas
    with EnumMetas
    with TextMetas
    with BooleanMetas
    with TemporalMetas

trait MetaCodecDerivation {
  implicit final def deriveMetaType[T](implicit c: Codec[T]): MetaType[T] = MetaType(c)
}
