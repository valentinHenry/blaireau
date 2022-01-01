// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances

import blaireau.Configuration
import blaireau.metas.{Meta, MetaS}

trait TextMetaInstances {
  implicit def stringMeta(implicit c: Configuration): MetaS[String] = Meta.of(c.stringCodec)
}
