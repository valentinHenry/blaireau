// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.meta.instances

import blaireau.Meta
import skunk.codec.UuidCodec

import java.util.UUID

trait UuidMetaInstances extends UuidCodec {
  implicit val uuidMeta: Meta[UUID] = Meta(uuid, Nil)
}
