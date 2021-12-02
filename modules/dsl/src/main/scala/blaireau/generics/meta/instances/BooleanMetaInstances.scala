// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.meta.instances

import blaireau.Meta
import skunk.codec.BooleanCodec

trait BooleanMetaInstances extends BooleanCodec {
  implicit val booleanMeta: Meta[Boolean] = Meta(bool)
}
