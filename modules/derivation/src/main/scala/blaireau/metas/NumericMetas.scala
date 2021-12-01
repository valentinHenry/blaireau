// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.Meta
import skunk.codec.NumericCodecs

object numeric extends NumericMetas

trait NumericMetas extends NumericCodecs {
  implicit val shortMeta: Meta[Short]           = Meta(int2)
  implicit val intMeta: Meta[Int]               = Meta(int4)
  implicit val longMeta: Meta[Long]             = Meta(int8)
  implicit val bigDecimalMeta: Meta[BigDecimal] = Meta(numeric, Nil)
  implicit val floatMeta: Meta[Float]           = Meta(float4)
  implicit val doubleMeta: Meta[Double]         = Meta(float8)
}
