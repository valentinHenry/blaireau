// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import skunk.Codec

trait MetaField {
  private[blaireau] type FieldType
  private[blaireau] def sqlName: String
  private[blaireau] def name: String
  private[blaireau] def codec: Codec[FieldType]
}

object MetaField {
  type Aux[T] = MetaField { type FieldType = T }
}

case class Meta[T](
  codec: Codec[T],
  fields: List[MetaField]
)

object Meta {
  def apply[T: Meta]: Meta[T] = implicitly

  def apply[T <: AnyVal](codec: Codec[T]): Meta[T] = Meta(codec, Nil)
}
