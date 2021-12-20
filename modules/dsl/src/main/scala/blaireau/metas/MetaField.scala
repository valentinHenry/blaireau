// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import shapeless.{::, HNil}
import skunk.Codec

// Representation of a db column.
trait MetaField[H] extends FieldProduct { self =>
  override type MF = MetaField[H] :: HNil
  override type T  = H

  private[blaireau] override def metaFields: MF = this :: HNil

  private[blaireau] def sqlName: String
  private[blaireau] def name: String
  private[blaireau] def codec: Codec[H]

  override def toString: String = s"MetaField($sqlName:$name:$codec)"
}
