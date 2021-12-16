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

// Representation of a db column.
trait MetaField[H] extends FieldProduct { self =>
  override type MF = MetaField[H] :: HNil
  override type T  = H

  private[blaireau] override def metaFields: MF = this :: HNil

  private[blaireau] def sqlName: String
  private[blaireau] def name: String
  private[blaireau] def codec: Codec[H]

  override def toString: String = s"MetaField($sqlName:$name:$codec)"
}

// Representation of a group of db columns
trait MetaElt[A] extends Dynamic with FieldProduct { self: Meta[A] =>
  import shapeless.record._
  override type T = A
  type F <: HList

  def column(k: Witness)(implicit s: Selector[MF, k.T]): s.Out = metaFields.get(k)

  // TODO macro: replace select dynamic by functions of the present fields (to help idea + the user)
  def selectDynamic(k: String)(implicit s: Selector[F, Symbol @@ k.type]): s.Out = fields.record.selectDynamic(k)
}

object MetaElt {
  type Aux[T0, F0, MF0] = MetaElt[T0] {
    type T  = T0
    type F  = F0
    type MF = MF0
  }
}

trait Meta[A] extends MetaElt[A] { self =>
  override type T = A
  type UT <: HList
  type EF <: HList // Extracted Fields ex: (MetaField[F1] -> F1) :: (MetaField[F2] -> F2) :: ... :: HNil

  def codec: Codec[A]
  private[blaireau] def fields: F      // Representation of the object (MetaFields + MetaElts)
  private[blaireau] def metaFields: MF // fields in the sql

  def imap[B](f: A => B)(g: B => A): Meta.Aux[B, F, MF, EF] =
    new Meta[B] {
      override final type T  = B
      override final type F  = self.F
      override final type MF = self.MF
      override final type EF = self.EF

      override final val codec: Codec[B]                  = self.codec.imap(f)(g)
      private[blaireau] override final val fields: F      = self.fields
      private[blaireau] override final val metaFields: MF = self.metaFields

      private[blaireau] override final def extract(t: B): EF = self.extract(g(t))
    }

  def gmap[B](implicit twiddler: Twiddler.Aux[B, A]): Meta.Aux[B, F, MF, EF] =
    imap(twiddler.from)(twiddler.to)

  override def toString: String = s"Meta($codec, $metaFields)"

  private[blaireau] def extract(t: T): EF
}

object Meta {
  type Aux[T0, F0, MF0, EF0] = Meta[T0] {
    type T  = T0
    type F  = F0
    type MF = MF0
    type EF = EF0
  }

  type ExtractedField[T] = (MetaField[T], T)

  def of[T](c: Codec[T]): MetaS[T] = Meta[T, HNil, HNil, HNil](c, HNil, HNil)(_ => HNil)

  def apply[T, F0 <: HList, MF0 <: HList, EF0 <: HList](
    _codec: Codec[T],
    _fields: F0,
    _metaFields: MF0
  )(
    _extract: T => EF0
  ): Meta.Aux[T, F0, MF0, EF0] = new Meta[T] {
    override final type F  = F0
    override final type MF = MF0
    override final type EF = EF0

    override final val codec: Codec[T]                  = _codec
    private[blaireau] override final val fields: F      = _fields
    private[blaireau] override final val metaFields: MF = _metaFields

    private[blaireau] override final def extract(t: T): EF0 = _extract(t)
  }

  implicit final def genericMetaEncoder[A, T, CT, F <: HList, MF <: HList, EF <: HList](implicit
    generic: LabelledGeneric.Aux[A, T],
    meta0: Lazy[Meta0.Aux[T, CT, F, MF, EF]],
    tw: Twiddler.Aux[A, CT]
  ): Meta.Aux[A, F, MF, EF] =
    meta0.value.meta.gmap

  trait Meta0[T] {
    type MetaT
    type MetaF <: HList
    type MetaMF <: HList
    type MetaEF <: HList
    def meta: Meta.Aux[MetaT, MetaF, MetaMF, MetaEF]
  }

  object Meta0 {
    type Aux[T, MT, F <: HList, MF <: HList, EF <: HList] = Meta0[T] {
      type MetaT  = MT
      type MetaF  = F
      type MetaMF = MF
      type MetaEF = EF
    }

    def apply[T, MT, F <: HList, MF <: HList, EF <: HList](m: Meta.Aux[MT, F, MF, EF]): Meta0.Aux[T, MT, F, MF, EF] =
      new Meta0[T] {
        override final type MetaT  = MT
        override final type MetaF  = F
        override final type MetaMF = MF
        override final type MetaEF = EF

        override final val meta: Meta.Aux[MetaT, MetaF, MetaMF, MetaEF] = m
      }

    implicit def base0[K <: Symbol, H](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[Meta.Aux[H, HNil, HNil, HNil]]
    ): Meta0.Aux[
      FieldType[K, H] :: HNil,
      H,
      FieldType[K, MetaField[H]] :: HNil,
      FieldType[K, MetaField[H]] :: HNil,
      ExtractedField[H] :: HNil
    ] = {
      val fieldName: String = w.value.name

      val metaField: FieldType[K, MetaField[H]] = field[K](
        new MetaField[H] {
          override val sqlName: String = fieldName
          override val name: String    = fieldName
          override val codec: Codec[H] = hMeta.value.codec
        }
      )

      Meta0(
        Meta(hMeta.value.codec, metaField :: HNil, metaField :: HNil)(h => (metaField -> h) :: HNil)
      )
    }

    implicit def base0Compound[K <: Symbol, H, HF <: HList, HMF <: HList, HEF <: HList](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[Meta.Aux[H, HF, HMF, HEF]],
      l: Last[HF] // Check HF is not empty
    ): Meta0.Aux[
      FieldType[K, H] :: HNil,
      H,
      FieldType[K, MetaElt.Aux[H, HF, HMF]] :: HNil,
      HMF,
      HEF
    ] = {
      val meta = hMeta.value

      Meta0(
        Meta[H, FieldType[K, MetaElt.Aux[H, HF, HMF]] :: HNil, HMF, HEF](
          meta.codec,
          field[K](meta) :: HNil,
          meta.metaFields
        )(meta.extract)
      )
    }

    implicit def hlistMeta0[
      A <: HList,   // The object Generic Representation
      AF <: HList,  // Fields of the object
      AMF <: HList, // MetaFields of the object
      AEF <: HList, // Extracted fields type of the object
      B <: HList,   // The previous elements of A without L
      BM,           // Meta type of the previous elements
      BF <: HList,  // Fields of the previous elements
      BMF <: HList, // MetaFields of the previous elements
      BEF <: HList, // Extracted Fields type of the previous elements
      LFT,          // LastElement FieldType
      L,            // Last element type
      K <: Symbol   // FieldName of the last element
    ](implicit
      init: Init.Aux[A, B],
      last: Last.Aux[A, LFT],
      aPrepend: Prepend.Aux[B, LFT :: HNil, A],
      ev: LFT =:= FieldType[K, L],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF, BMF, BEF],
      lMeta: Lazy[Meta.Aux[L, HNil, HNil, HNil]],
      fPrepend: Prepend.Aux[BF, FieldType[K, MetaField[L]] :: HNil, AF],
      mfPrepend: Prepend.Aux[BMF, FieldType[K, MetaField[L]] :: HNil, AMF],
      efPrepend: Prepend.Aux[BEF, ExtractedField[L] :: HNil, AEF]
    ): Meta0.Aux[A, BM ~ L, AF, AMF, AEF] = {
      val fieldName: String = w.value.name

      // Simple type
      val metaField =
        new MetaField[L] {
          override def sqlName: String = fieldName
          override def name: String    = fieldName
          override def codec: Codec[L] = lMeta.value.codec
        }

      val metaFieldF: FieldType[K, MetaField[L]] = field[K](metaField)

      val codec = previous.meta.codec ~ metaField.codec

      Meta0(
        Meta(
          _codec = codec,
          _fields = previous.meta.fields ::: (metaFieldF :: HNil),
          _metaFields = previous.meta.metaFields ::: (metaFieldF :: HNil)
        ) { case (b, l) => previous.meta.extract(b) ::: ((metaField -> l) :: HNil) }
      )
    }

    implicit def hlistMeta0Compound[
      A <: HList,   // The object Generic Representation
      AF <: HList,  // Fields of the object
      AMF <: HList, // MetaFields of the object
      AEF <: HList, // Extracted field type of the object
      B <: HList,   // The previous elements of A without L
      BM,           // Meta type of the previous elements
      BF <: HList,  // Fields of the previous elements
      BMF <: HList, // MetaFields of the previous elements
      BEF <: HList, // Extracted fields of the previous elements
      LFT,          // Last element FieldType
      L,            // Last element type
      LF <: HList,  // Fields of the last element
      LMF <: HList, // MetaFields of the last element HNil in this case
      LEF <: HList, // Extracted field type of the last element
      K <: Symbol   // FieldName of the last element
    ](implicit
      init: Init.Aux[A, B],
      last: Last.Aux[A, LFT],
      aPrepend: Prepend.Aux[B, LFT :: HNil, A],
      ev: LFT =:= FieldType[K, L],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF, BMF, BEF],
      lMeta: Lazy[Meta.Aux[L, LF, LMF, LEF]],
      nonEmptyLF: Last[LF],
      fPrepend: Prepend.Aux[BF, FieldType[K, MetaElt.Aux[L, LF, LMF]] :: HNil, AF],
      mfPrepend: Prepend.Aux[BMF, LMF, AMF],
      efPrepend: Prepend.Aux[BEF, LEF, AEF]
    ): Meta0.Aux[A, BM ~ L, AF, AMF, AEF] = {
      val meta: Meta.Aux[L, LF, LMF, LEF]                = lMeta.value
      val metaElt: FieldType[K, MetaElt.Aux[L, LF, LMF]] = field[K](meta)
      val codec: Codec[BM ~ L]                           = previous.meta.codec ~ meta.codec

      Meta0(
        Meta(
          _codec = codec,
          _fields = previous.meta.fields ::: (metaElt :: HNil),
          _metaFields = previous.meta.metaFields ::: meta.metaFields
        ) { case (b, l) =>
          previous.meta.extract(b) ::: meta.extract(l)
        }
      )
    }
  }
}
