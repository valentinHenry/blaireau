// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances

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
  // FIXME EF is not right
//  implicit final def deriveOptionalMetaType[T, F <: HList, MF <: HList, EF <: HList](implicit
//    m: Meta.Aux[T, F, MF, EF]
//  ): Meta.Aux[Option[T], F, MF, Option[EF]] =
//    Meta(m.codec.opt, m.fields, m.metaFields)(_.map(m.extract))
}
