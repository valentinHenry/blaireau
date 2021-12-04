// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import shapeless.{HList, HNil}
import skunk.Codec

import scala.language.implicitConversions

trait MetaField[H] {
  private[blaireau] def sqlName: String
  private[blaireau] def name: String
  private[blaireau] def codec: Codec[H]
}

trait Meta[T] { self =>
  type F <: HList

  def codec: Codec[T]
  def fieldNames: List[String]
  def metaFields: F

  def imap[B](f: T => B)(g: B => T): Meta.Aux[B, F] =
    new Meta[B] {
      type F = self.F
      def codec: Codec[B]          = self.codec.imap(f)(g)
      def fieldNames: List[String] = self.fieldNames
      def metaFields: F            = self.metaFields
    }
}

object Meta {
  type Aux[T, FieldsT <: HList] = Meta[T] { type F = FieldsT }

  def getFields[T, F <: HList](meta: Meta.Aux[T, F]): F = meta.metaFields

  def apply[T, FieldsT <: HList](c: Codec[T], f: List[String], mf: FieldsT): Meta.Aux[T, FieldsT] = new Meta[T] {
    override type F = FieldsT
    override def codec: Codec[T]          = c
    override def fieldNames: List[String] = f
    override def metaFields: FieldsT      = mf
  }

}
