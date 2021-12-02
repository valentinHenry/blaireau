// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec.instances

import skunk.Codec
import skunk.codec.UuidCodec

import java.util.UUID

trait UuidCodecInstances extends UuidCodec {
  implicit val uuidCodec: Codec[UUID] = uuid
}
