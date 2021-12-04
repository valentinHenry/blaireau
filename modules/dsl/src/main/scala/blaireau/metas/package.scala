// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import shapeless.{HList, HNil}
import skunk.{Codec, Void}

package object metas {
  implicit val hnilMeta: Meta.Aux[HNil, HNil] = Meta[HNil, HNil](
    Void.codec.imap(_ => ???)(_ => Void), //FIXME
    List.empty,
    HNil
  )

  implicit val hnilExportedMeta: ExportedMeta.Aux[HNil, HNil, HNil] = ExportedMeta(hnilMeta)

  implicit final def optionMeta[T, F <: HList](implicit m: Meta.Aux[T, F]): Meta.Aux[Option[T], F] =
    Meta[Option[T], F](m.codec.opt, m.fieldNames, m.metaFields)

  implicit final def codecToMeta[T](implicit c: Codec[T]): Meta.Aux[T, HNil] = Meta[T, HNil](c, Nil, HNil)
}
