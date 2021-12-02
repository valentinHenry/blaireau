// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.meta.instances

import blaireau.Meta
import skunk.Codec

object all extends AllMetaInstances

trait AllMetaInstances
    extends MetaDerivation
    with NumericMetaInstances
    with BinaryMetaInstances
    with UuidMetaInstances
    with EnumMetaInstances
    with TextMetaInstances
    with BooleanMetaInstances
    with TemporalMetaInstances

trait MetaDerivation {
  implicit final def deriveOptionalMetaType[T](implicit m: Meta[T]): Meta[Option[T]] = Meta(m.codec.opt, m.fields)
}
