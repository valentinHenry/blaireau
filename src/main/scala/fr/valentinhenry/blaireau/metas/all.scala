package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.Meta
import skunk.Codec

object all extends AllMetas

trait AllMetas
    extends MetaDerivation
    with NumericMetas
    with BinaryMetas
    with UuidMetas
    with EnumMetas
    with TextMetas
    with BooleanMetas
    with TemporalMetas

trait MetaDerivation {
  implicit final def deriveOptionalMetaType[T](implicit m: Meta[T]): Meta[Option[T]] = Meta(m.opt, m.fields)
}
