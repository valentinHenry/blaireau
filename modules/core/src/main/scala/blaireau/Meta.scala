// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import skunk.Codec

trait MetaField {
  type T
  def name: String
}

case class Meta[T](
  codec: Codec[T],
  fields: List[MetaField]
)

object Meta {
  def apply[T <: AnyVal](codec: Codec[T]): Meta[T] = Meta(codec, Nil)
}
