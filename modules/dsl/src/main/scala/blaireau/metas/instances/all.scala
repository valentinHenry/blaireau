// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances

object all extends AllMetaInstances

trait AllMetaInstances
  extends NumericMetaInstances
  with BinaryMetaInstances
  with UuidMetaInstances
  with EnumMetaInstances
  with TextMetaInstances
  with BooleanMetaInstances
  with TemporalMetaInstances
