// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances

import blaireau.metas.{Meta, MetaS}
import skunk.codec.NumericCodecs

object numeric extends NumericCodecInstances

trait NumericCodecInstances extends NumericCodecs {
  implicit val shortMeta: MetaS[Short]           = Meta.of(int2)
  implicit val intMeta: MetaS[Int]               = Meta.of(int4)
  implicit val longMeta: MetaS[Long]             = Meta.of(int8)
  implicit val bigDecimalMeta: MetaS[BigDecimal] = Meta.of(numeric)
  implicit val floatMeta: MetaS[Float]           = Meta.of(float4)
  implicit val doubleMeta: MetaS[Double]         = Meta.of(float8)
}
