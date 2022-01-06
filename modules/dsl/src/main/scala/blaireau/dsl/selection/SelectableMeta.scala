// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.selection

import blaireau.metas.{FieldProduct, Meta}
import shapeless.HList
import shapeless.tag.@@
import skunk.Codec

trait SelectableMeta[T, F <: HList, MF <: HList] extends Dynamic with FieldProduct[T, MF] {
  private[blaireau] def fields: F

  // TODO macro: replace select dynamic by functions of the present fields (to help idea + the user)
  def selectDynamic(k: String)(implicit s: SelectableSelector[F, Symbol @@ k.type]): s.Out = s(fields)
}

object SelectableMeta {
  def make[T, F <: HList, MF <: HList, EF <: HList](meta: Meta.Aux[T, F, MF, EF]): SelectableMeta[T, F, MF] =
    new SelectableMeta[T, F, MF] {
      override private[blaireau] def fields: F = meta.fields

      override private[blaireau] def metaFields: MF = meta.metaFields

      override private[blaireau] def codec: Codec[T] = meta.codec
    }
}
