// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.Meta
import skunk.codec.TextCodecs

object text extends TextMetas

trait TextMetas extends TextCodecs {
  //TODO find a solution for bpchar and varchar...
  implicit val stringMeta: Meta[String] = Meta(text, Nil)
}
