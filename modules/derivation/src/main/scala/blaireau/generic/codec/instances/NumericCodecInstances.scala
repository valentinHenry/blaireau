// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec.instances

import skunk.Codec
import skunk.codec.NumericCodecs

object numeric extends NumericCodecInstances

trait NumericCodecInstances extends NumericCodecs {
  implicit val shortCodec: Codec[Short]           = int2
  implicit val intCodec: Codec[Int]               = int4
  implicit val longCodec: Codec[Long]             = int8
  implicit val bigDecimalCodec: Codec[BigDecimal] = numeric
  implicit val floatCodec: Codec[Float]           = float4
  implicit val doubleCodec: Codec[Double]         = float8
}
