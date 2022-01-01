// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances.circe

import blaireau.metas.{Meta, MetaS}
import io.circe.{Decoder, Encoder, Json}
import skunk.circe.codec.all._

trait CirceMetaInstances {
  implicit final val jsonbMeta: MetaS[Json] = Meta.of(jsonb)

  def asJsonbMeta[A: Encoder: Decoder]: MetaS[A] = Meta.of(jsonb[A])

  def asJsonMeta[A: Encoder: Decoder]: MetaS[A] = Meta.of(json[A])
}
