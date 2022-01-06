// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import shapeless.HList
import shapeless.ops.hlist.Prepend
import skunk.{Codec, ~}

trait FieldProduct[T, MF <: HList] {
  self =>
  private[blaireau] def metaFields: MF

  private[blaireau] def codec: Codec[T]

  def ~[RT, RMF <: HList, OT, OMF <: HList](
    right: FieldProduct[RT, RMF]
  )(implicit prepend: Prepend.Aux[MF, RMF, OMF]): FieldProduct[T ~ RT, OMF] =
    new FieldProduct[T ~ RT, OMF] {
      private[blaireau] override def metaFields: OMF = self.metaFields ::: right.metaFields

      private[blaireau] override def codec: Codec[T ~ RT] = self.codec ~ right.codec
    }

}
