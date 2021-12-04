// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import shapeless.{HList, HNil, LabelledGeneric}
import skunk.Codec

import scala.language.implicitConversions

trait MetaField[H] {
  private[blaireau] def sqlName: String
  private[blaireau] def name: String
  private[blaireau] def codec: Codec[H]
}

trait Meta[A] { self =>
  type F <: HList

  def codec: Codec[A]
  def fieldNames: List[String]
  def metaFields: F

  def imap[B](f: A => B)(g: B => A): Meta.Aux[B, F] =
    new Meta[B] {
      type F = self.F
      def codec: Codec[B]          = self.codec.imap(f)(g)
      def fieldNames: List[String] = self.fieldNames
      def metaFields: F            = self.metaFields
    }
}

object Meta {
  type Aux[T, FieldsT <: HList] = Meta[T] { type F = FieldsT }

  def apply[T, FieldsT <: HList](c: Codec[T], f: List[String], mf: FieldsT): Meta.Aux[T, FieldsT] = new Meta[T] {
    override type F = FieldsT
    override def codec: Codec[T]          = c
    override def fieldNames: List[String] = f
    override def metaFields: FieldsT      = mf
  }

}

trait ExportedMeta[T] {
  type MetaT
  type MetaF <: HList
  def meta: Meta.Aux[MetaT, MetaF]

  def imap[B, C](f: MetaT => C)(g: C => MetaT)(implicit ev: LabelledGeneric.Aux[B, T]): ExportedMeta.Aux[B, C, MetaF] =
    ExportedMeta[B, C, MetaF](meta.imap(f)(g))
}

object ExportedMeta {
  type Aux[T, M, MF <: HList] = ExportedMeta[T] {
    type MetaT = M
    type MetaF = MF
  }

  def apply[T, M, MF <: HList](m: Meta.Aux[M, MF]): ExportedMeta.Aux[T, M, MF] =
    new ExportedMeta[T] {
      override type MetaT = M
      override type MetaF = MF
      override def meta: Meta.Aux[M, MF] = m
    }

}
