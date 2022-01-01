// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

trait OptionalMetaField[H] extends MetaField[Option[H]] {
  private[blaireau] def internal: MetaField[H]
}
