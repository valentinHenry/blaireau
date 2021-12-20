// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import shapeless.HList
import shapeless.ops.hlist.Prepend
import skunk.{Codec, ~}

trait FieldProduct { self =>
  type MF <: HList
  type T

  private[blaireau] def metaFields: MF
  private[blaireau] def codec: Codec[T]

  def ~[RT, RMF <: HList, OT, OMF <: HList](
    right: FieldProduct.Aux[RT, RMF]
  )(implicit prepend: Prepend.Aux[MF, RMF, OMF]): FieldProduct.Aux[self.T ~ RT, OMF] =
    new FieldProduct {
      type MF = OMF
      type T  = self.T ~ RT
      private[blaireau] override def metaFields: MF = self.metaFields ::: right.metaFields

      private[blaireau] override def codec: Codec[T] = self.codec ~ right.codec
    }

}
object FieldProduct {
  type Aux[T0, MF0] = FieldProduct {
    type T  = T0
    type MF = MF0
  }
}
