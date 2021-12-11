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

// Representation of a db column.
trait MetaField[H] {
  private[blaireau] def sqlName: String
  private[blaireau] def name: String
  private[blaireau] def codec: Codec[H]

  override def toString: String = s"MetaField($sqlName:$name:$codec)"
}

// Representation of a group of db columns
trait MetaElt[T] extends Dynamic { self: Meta[T] =>
  type F <: HList
  import shapeless.record._
  def column(k: Witness)(implicit s: Selector[F, k.T]): s.Out = fields.get(k)
  def apply(k: Witness)(implicit s: Selector[F, k.T]): s.Out  = fields.get(k)

  // TODO macro: replace select dynamic by functions of the present fields (to help idea + the user)
  def selectDynamic(k: String)(implicit s: Selector[F, Symbol @@ k.type]): s.Out = fields.record.selectDynamic(k)
}

object MetaElt {
  type Aux[T, F0] = MetaElt[T] { type F = F0 }
}

trait Meta[A] extends MetaElt[A] { self =>
  type F <: HList
  type MF <: HList

  def codec: Codec[A]
  def fields: F      // Representation of the object (MetaFields + MetaElts)
  def metaFields: MF // fields in the sql

  def imap[B](f: A => B)(g: B => A): Meta.Aux[B, F, MF] =
    new Meta[B] {
      override final type F  = self.F
      override final type MF = self.MF

      override final def codec: Codec[B] = self.codec.imap(f)(g)
      override final def fields: F       = self.fields
      override final def metaFields: MF  = self.metaFields
    }

  def gmap[B](implicit twiddler: Twiddler.Aux[B, A]): Meta.Aux[B, F, MF] =
    imap(twiddler.from)(twiddler.to)

  override def toString: String = s"Meta($codec, $metaFields)"
}

object Meta {
  type Aux[T0, F0, MF0] = Meta[T0] {
    type F  = F0
    type MF = MF0
  }

  def of[T](c: Codec[T]): MetaS[T] = Meta[T, HNil, HNil](c, HNil, HNil)

  def apply[T, F0 <: HList, MF0 <: HList](c: Codec[T], f: F0, mf: MF0): Meta.Aux[T, F0, MF0] = new Meta[T] {
    override final type F  = F0
    override final type MF = MF0

    override final val codec: Codec[T] = c
    override final val fields: F       = f
    override final val metaFields: MF  = mf
  }

  implicit final def genericMetaEncoder[A, T, CT, F <: HList, MF <: HList](implicit
    generic: LabelledGeneric.Aux[A, T],
    meta0: Lazy[Meta0.Aux[T, CT, F, MF]],
    tw: Twiddler.Aux[A, CT]
  ): Meta.Aux[A, F, MF] =
    meta0.value.meta.gmap

  trait Meta0[T] {
    type MetaT
    type MetaF <: HList
    type MetaMF <: HList
    def meta: Meta.Aux[MetaT, MetaF, MetaMF]
  }

  object Meta0 {
    type Aux[T, MT, F <: HList, MF <: HList] = Meta0[T] {
      type MetaT  = MT
      type MetaF  = F
      type MetaMF = MF
    }

    def apply[T, MT, F <: HList, MF <: HList](m: Meta.Aux[MT, F, MF]): Meta0.Aux[T, MT, F, MF] = new Meta0[T] {
      override final type MetaT  = MT
      override final type MetaF  = F
      override final type MetaMF = MF

      override val meta: Meta.Aux[MetaT, MetaF, MetaMF] = m
    }

    implicit def base0[K <: Symbol, H](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[Meta.Aux[H, HNil, HNil]]
    ): Meta0.Aux[FieldType[K, H] :: HNil, H, FieldType[K, MetaField[H]] :: HNil, FieldType[K, MetaField[H]] :: HNil] = {
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
          metaField :: HNil,
          metaField :: HNil
        )
      )
    }

    implicit def base0Compound[K <: Symbol, H, HF <: HList, HMF <: HList](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[Meta.Aux[H, HF, HMF]],
      l: Last[HF] // Check HF is not empty
    ): Meta0.Aux[FieldType[K, H] :: HNil, H, FieldType[K, MetaElt.Aux[H, HF]] :: HNil, HMF] = {
      val meta = hMeta.value

      Meta0(
        Meta(
          meta.codec,
          field[K](meta) :: HNil,
          meta.metaFields
        )
      )
    }

    implicit def hlistMeta0[
      A <: HList,   // The object Generic Representation
      B <: HList,   // The previous elements of A without L
      LFT,          // LastElement FieldType
      L,            // Last element type
      K <: Symbol,  // FieldName of the last element
      BM,           // Meta type of the previous elements
      AF <: HList,  // Fields of the object
      BF <: HList,  // Fields of the previous elements
      AMF <: HList, // MetaFields of the object
      BMF <: HList  // MetaFields of the previous elements
    ](implicit
      init: Init.Aux[A, B],
      last: Last.Aux[A, LFT],
      aPrepend: Prepend.Aux[B, LFT :: HNil, A],
      ev: LFT =:= FieldType[K, L],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF, BMF],
      lMeta: Lazy[Meta.Aux[L, HNil, HNil]],
      fPrepend: Prepend.Aux[BF, FieldType[K, MetaField[L]] :: HNil, AF],
      mfPrepend: Prepend.Aux[BMF, FieldType[K, MetaField[L]] :: HNil, AMF]
    ): Meta0.Aux[A, BM ~ L, AF, AMF] = {
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
          f = previous.meta.fields ::: (metaField :: HNil),
          mf = previous.meta.metaFields ::: (metaField :: HNil)
        )
      )
    }

    implicit def hlistMeta0Compound[
      A <: HList,   // The object Generic Representation
      B <: HList,   // The previous elements of A without L
      LFT,          // Last element FieldType
      L,            // Last element type
      K <: Symbol,  // FieldName of the last element
      BM,           // Meta type of the previous elements
      AF <: HList,  // Fields of the object
      BF <: HList,  // Fields of the previous elements
      LF <: HList,  // Fields of the last element
      AMF <: HList, // MetaFields of the object
      BMF <: HList, // MetaFields of the previous elements
      LMF <: HList  // MetaFields of the last element HNil in this case
    ](implicit
      init: Init.Aux[A, B],
      last: Last.Aux[A, LFT],
      aPrepend: Prepend.Aux[B, LFT :: HNil, A],
      ev: LFT =:= FieldType[K, L],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF, BMF],
      lMeta: Lazy[Meta.Aux[L, LF, LMF]],
      nonEmptyLF: Last[LF],
      fPrepend: Prepend.Aux[BF, FieldType[K, MetaElt.Aux[L, LF]] :: HNil, AF],
      mfPrepend: Prepend.Aux[BMF, LMF, AMF]
    ): Meta0.Aux[A, BM ~ L, AF, AMF] = {
      val meta: Meta.Aux[L, LF, LMF]                = lMeta.value
      val metaElt: FieldType[K, MetaElt.Aux[L, LF]] = field[K](meta)
      val codec: Codec[BM ~ L]                      = previous.meta.codec ~ meta.codec

      Meta0(
        Meta(
          c = codec,
          f = previous.meta.fields ::: (metaElt :: HNil),
          mf = previous.meta.metaFields ::: meta.metaFields
        )
      )
    }
  }
}
