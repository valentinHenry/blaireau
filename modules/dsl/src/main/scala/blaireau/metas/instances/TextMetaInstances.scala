// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances

import blaireau.metas.{Meta, MetaS}
import skunk.codec.TextCodecs

object text extends TextCodecInstances

trait TextCodecInstances extends TextCodecs {
  // TODO find a solution for bpchar and varchar...
  implicit val stringMeta: MetaS[String] = Meta.of(text)
}
