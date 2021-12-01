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

case class Meta[T](
  codec: Codec[T],
  fields: List[MetaField]
) extends DbElt[T]

trait DbElt[T] { self: Meta[T] =>
  def selectDynamic(name: String): MetaField =
    fields.find(_.name == name) match {
      case Some(value) => value
      case None        => throw new UnsupportedOperationException("This field does not exist")
    }
}

object Meta {
  def apply[T: Meta]: Meta[T] = implicitly

  def apply[T <: AnyVal](codec: Codec[T]): Meta[T] = Meta(codec, Nil)
}
