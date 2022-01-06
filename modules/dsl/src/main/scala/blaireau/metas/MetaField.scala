// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import shapeless.{::, HNil}
import skunk.Codec

import java.util.UUID

// Representation of a db column.
trait MetaField[H] extends FieldProduct[H, MetaField[H] :: HNil] {
  self =>

  private[blaireau] override def metaFields: MetaField[H] :: HNil = self :: HNil

  private[blaireau] def sqlName: String

  private[blaireau] def name: String

  private[blaireau] val id: UUID

  private[blaireau] def codec: Codec[H]

  def opt: OptionalMetaField[H] =
    new OptionalMetaField[H] {
      override private[blaireau] final val sqlName: String         = self.sqlName
      override private[blaireau] final val name: String            = self.name
      override private[blaireau] final val codec: Codec[Option[H]] = self.codec.opt

      override private[blaireau] final val internal: MetaField[H] = self
      override private[blaireau] final val id: UUID               = self.id
    }

  override def toString: String = s"MetaField($sqlName:$name:$codec)"
}
