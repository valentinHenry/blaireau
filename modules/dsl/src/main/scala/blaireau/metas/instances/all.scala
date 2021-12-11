// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances

import blaireau.metas.Meta
import shapeless.HList

object all extends AllMetaInstances

trait AllMetaInstances
    extends CodecDerivation
    with NumericCodecInstances
    with BinaryMetaInstances
    with UuidMetaInstances
    with EnumMetaInstances
    with TextCodecInstances
    with BooleanMetaInstances
    with TemporalMetaInstances

trait CodecDerivation {
  implicit final def deriveOptionalMetaType[T, F <: HList, MF <: HList](implicit
    m: Meta.Aux[T, F, MF]
  ): Meta.Aux[Option[T], F, MF] =
    Meta(m.codec.opt, m.fields, m.metaFields)
}
