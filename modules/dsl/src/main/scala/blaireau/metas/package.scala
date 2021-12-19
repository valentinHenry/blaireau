// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import shapeless.HNil

package object metas {
  type MetaS[T] = Meta.Aux[T, HNil, HNil, HNil]
}
