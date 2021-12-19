// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec.instances

import skunk.Codec
import skunk.codec.BooleanCodec

trait BooleanCodecInstances extends BooleanCodec {
  implicit val booleanCodec: Codec[Boolean] = bool
}
