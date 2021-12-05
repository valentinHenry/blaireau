// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import shapeless.labelled.{FieldType, field}
import shapeless.ops.hlist.{Init, Last, Prepend}
import shapeless.ops.record.Selector
import shapeless.tag.@@
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}
import skunk.{Codec, ~}
import skunk.util.Twiddler

import scala.language.experimental.macros
import scala.language.{dynamics, implicitConversions}

trait MetaField[H] {
  private[blaireau] def sqlName: String
  private[blaireau] def name: String
  private[blaireau] def codec: Codec[H]

  override def toString: String = s"MetaField($sqlName:$name:$codec)"
}

trait Meta[A] extends TableSchema[A] { self =>
  def codec: Codec[A]
  type F <: HList
  def metaFields: F

  def imap[B](f: A => B)(g: B => A): Meta.Aux[B, F] =
    new Meta[B] {
      type F = self.F
      def codec: Codec[B] = self.codec.imap(f)(g)
      def metaFields: F   = self.metaFields
    }

  def gmap[B](implicit twiddler: Twiddler.Aux[B, A]): Meta.Aux[B, F] =
    imap(twiddler.from)(twiddler.to)

  override def toString: String = s"Meta($codec, $metaFields)"
}

object Meta {
  type Aux[T0, F0] = Meta[T0] { type F = F0 }

  def of[T](c: Codec[T]): MetaS[T] = Meta[T, HNil](c, HNil)

  def apply[T, F0 <: HList](c: Codec[T], mf: F0): Meta.Aux[T, F0] = new Meta[T] {
    override final type F = F0
    override final val codec: Codec[T] = c
    override final val metaFields: F   = mf
  }

  implicit final def genericMetaEncoder[A, T, CT, F <: HList](implicit
    generic: LabelledGeneric.Aux[A, T],
    meta0: Lazy[Meta0.Aux[T, CT, F]],
    tw: Twiddler.Aux[A, CT]
  ): Meta.Aux[A, F] =
    meta0.value.meta.gmap

  trait Meta0[T] {
    type MetaT
    type MetaF <: HList
    def meta: Meta.Aux[MetaT, MetaF]
  }

  object Meta0 {
    type Aux[T, MT, MF <: HList] = Meta0[T] {
      type MetaT = MT
      type MetaF = MF
    }

    def apply[T, MT, MF <: HList](m: Meta.Aux[MT, MF]): Meta0.Aux[T, MT, MF] = new Meta0[T] {
      override type MetaT = MT
      override type MetaF = MF

      override val meta: Meta.Aux[MetaT, MetaF] = m
    }

    implicit def base0[K <: Symbol, H](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[Meta.Aux[H, HNil]]
    ): Meta0.Aux[FieldType[K, H] :: HNil, H, FieldType[K, MetaField[H]] :: HNil] = {
      val fieldName: String = w.value.name

      val metaField: FieldType[K, MetaField[H]] = field[K](
        new MetaField[H] {
          override def sqlName: String = fieldName
          override def name: String    = fieldName
          override def codec: Codec[H] = hMeta.value.codec
        }
      )

      Meta0(
        Meta(
          hMeta.value.codec,
          metaField :: HNil
        )
      )
    }

    implicit def base0Compound[K <: Symbol, H, HF <: HList](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[Meta.Aux[H, HF]],
      l: Last[HF] // Check HF is not empty
    ): Meta0.Aux[FieldType[K, H] :: HNil, H, HF] =
      Meta0(hMeta.value)

    implicit def hlistMeta0[
      A <: HList, // The object Generic Representation
      B <: HList, // The previous elements of A without L
      LFT,
      L,           // Last element type
      K <: Symbol, // FieldName of the last element
      BM,          // Meta type of the previous elements
      AF <: HList, // Fields of the object
      BF <: HList  // Fields of the previous elements
//    LF <: HList   // Fields of the last element HNil in this case
    ](implicit
      init: Init.Aux[A, B],
      last: Last.Aux[A, LFT],
      aPrepend: Prepend.Aux[B, LFT :: HNil, A],
      ev: LFT =:= FieldType[K, L],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF],
      lMeta: Lazy[Meta.Aux[L, HNil]],
      fPrepend: Prepend.Aux[BF, FieldType[K, MetaField[L]] :: HNil, AF]
    ): Meta0.Aux[A, BM ~ L, AF] = {
      val fieldName: String = w.value.name

      // Simple type
      val metaField: FieldType[K, MetaField[L]] = field[K](
        new MetaField[L] {
          override def sqlName: String = fieldName
          override def name: String    = fieldName
          override def codec: Codec[L] = lMeta.value.codec
        }
      )

      val codec = previous.meta.codec ~ metaField.codec

      Meta0(
        Meta(
          c = codec,
          mf = previous.meta.metaFields ::: (metaField :: HNil)
        )
      )
    }

    implicit def hlistMeta0Compound[
      A <: HList, // The object Generic Representation
      B <: HList, // The previous elements of A without L
      LFT,
      L,           // Last element type
      K <: Symbol, // FieldName of the last element
      BM,          // Meta type of the previous elements
      AF <: HList, // Fields of the object
      BF <: HList, // Fields of the previous elements
      LF <: HList  // Fields of the last element
    ](implicit
      init: Init.Aux[A, B],
      last: Last.Aux[A, LFT],
      aPrepend: Prepend.Aux[B, LFT :: HNil, A],
      ev: LFT =:= FieldType[K, L],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF],
      lMeta: Lazy[Meta.Aux[L, LF]],
      nonEmptyLF: Last[LF],
      fPrepend: Prepend.Aux[BF, LF, AF]
    ): Meta0.Aux[A, BM ~ L, AF] = {
      val codec = previous.meta.codec ~ lMeta.value.codec

      Meta0(
        Meta(
          c = codec,
          mf = previous.meta.metaFields ::: lMeta.value.metaFields
        )
      )
    }
  }
}

trait TableSchema[T] extends Dynamic { self: Meta[T] =>
  type F <: HList
  import shapeless.record._
  def column(k: Witness)(implicit s: Selector[F, k.T]): s.Out = metaFields.get(k)
  def apply(k: Witness)(implicit s: Selector[F, k.T]): s.Out  = metaFields.get(k)

  // TODO macro: replace select dynamic by functions of the present fields (to help idea + the user)
  def selectDynamic(k: String)(implicit s: Selector[F, Symbol @@ k.type]): s.Out = metaFields.record.selectDynamic(k)
}

object TableSchema {
  type Aux[T, F0] = TableSchema[T] { type F = F0 }
}
