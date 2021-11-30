// Written by Valentin HENRY
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.Meta
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
