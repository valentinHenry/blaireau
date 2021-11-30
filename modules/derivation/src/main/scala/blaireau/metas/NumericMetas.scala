// Written by Valentin HENRY
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.Meta
import skunk.codec.NumericCodecs

trait NumericMetas extends NumericCodecs {
  implicit val shortMeta: Meta[Short]           = Meta(int2, Nil)
  implicit val intMeta: Meta[Int]               = Meta(int4, Nil)
  implicit val longMeta: Meta[Long]             = Meta(int8, Nil)
  implicit val bigDecimalMeta: Meta[BigDecimal] = Meta(numeric, Nil)
  implicit val floatMeta: Meta[Float]           = Meta(float4, Nil)
  implicit val doubleMeta: Meta[Double]         = Meta(float8, Nil)
}
