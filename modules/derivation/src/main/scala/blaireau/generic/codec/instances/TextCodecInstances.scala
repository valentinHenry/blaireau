// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec.instances

import skunk.Codec
import skunk.codec.TextCodecs

object text extends TextCodecInstances

trait TextCodecInstances extends TextCodecs {
  //TODO find a solution for bpchar and varchar...
  implicit val stringCodec: Codec[String] = text
}
