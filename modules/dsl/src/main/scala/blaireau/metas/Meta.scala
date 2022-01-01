// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.Configuration
import shapeless.labelled.{FieldType, field}
import shapeless.ops.hlist.{Init, Last, Prepend}
import shapeless.ops.record.Selector
import shapeless.tag.@@
import shapeless.{::, <:!<, HList, HNil, LabelledGeneric, Lazy, Witness}
import skunk.util.Twiddler
import skunk.{Codec, ~}

import scala.annotation.nowarn

trait Meta[A] extends FieldProduct with Dynamic {
  self =>

  import shapeless.record._

  override type T = A
  type F <: HList
  type EF <: HList // Extracted Fields ex: (MetaField[F1] -> F1) :: (MetaField[F2] -> F2) :: ... :: HNil
  type MF <: HList

  def codec: Codec[A]

  private[blaireau] def fields: F // Representation of the object (MetaFields + Metas)

  private[blaireau] def metaFields: MF // fields in the sql

  override def toString: String = s"Meta($codec, $metaFields)"

  private[blaireau] def extract(t: T): EF

  def gmap[B](implicit twiddler: Twiddler.Aux[B, A]): Meta.Aux[B, F, MF, EF] =
    imap(twiddler.from)(twiddler.to)

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

  // TODO macro: replace select dynamic by functions of the present fields (to help idea + the user)
  def selectDynamic(k: String)(implicit s: Selector[F, Symbol @@ k.type]): s.Out = fields.record.selectDynamic(k)
}

object Meta {

  type Aux[T0, F0 <: HList, MF0 <: HList, EF0 <: HList] = Meta[T0] {
    type T  = T0
    type F  = F0
    type MF = MF0
    type EF = EF0
  }

  implicit def optionalMetaS[T](implicit m: MetaS[T]): MetaS[Option[T]] =
    of(m.codec.opt)

  trait Meta0[T] {
    type MetaT
    type MetaF <: HList
    type MetaMF <: HList
    type MetaEF <: HList

    def meta: Meta.Aux[MetaT, MetaF, MetaMF, MetaEF]
  }

  def of[T](c: Codec[T]): MetaS[T] = Meta[T, HNil, HNil, HNil](c, HNil, HNil)(_ => HNil)

  @nowarn("cat=unused")
  implicit final def genericMetaEncoder[A, T, CT, F <: HList, MF <: HList, EF <: HList](implicit
    generic: LabelledGeneric.Aux[A, T],
    meta0: Lazy[Meta0.Aux[T, CT, F, MF, EF]],
    tw: Twiddler.Aux[A, CT]
  ): Meta.Aux[A, F, MF, EF] =
    meta0.value.meta.gmap

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

  object Meta0 {
    type Aux[T, MT, F <: HList, MF <: HList, EF <: HList] = Meta0[T] {
      type MetaT  = MT
      type MetaF  = F
      type MetaMF = MF
      type MetaEF = EF
    }

    def apply[T, MT, F <: HList, MF <: HList, EF <: HList](
      codec: Codec[MT],
      fields: F,
      metaFields: MF
    )(
      extract: MT => EF
    ): Meta0.Aux[T, MT, F, MF, EF] =
      new Meta0[T] {
        override final type MetaT  = MT
        override final type MetaF  = F
        override final type MetaMF = MF
        override final type MetaEF = EF

        override final val meta: Meta.Aux[MetaT, MetaF, MetaMF, MetaEF] =
          Meta(codec, fields, metaFields)(extract)
      }

    @nowarn("cat=unused")
    implicit def baseField[K <: Symbol, H](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[MetaS[H]],
      evNotOption: H <:!< Option[_],
      c: Configuration
    ): Meta0.Aux[
      FieldType[K, H] :: HNil,
      H,
      FieldType[K, MetaField[H]] :: HNil,
      MetaField[H] :: HNil,
      ExtractedField[H] :: HNil
    ] = {
      val fieldName: String = w.value.name
      val metaField = new MetaField[H] {
        override val sqlName: String = c.fieldFormatter.formatField(fieldName)
        override val name: String    = fieldName
        override val codec: Codec[H] = hMeta.value.codec
      }

      Meta0[
        FieldType[K, H] :: HNil,
        H,
        FieldType[K, MetaField[H]] :: HNil,
        MetaField[H] :: HNil,
        ExtractedField[H] :: HNil
      ](
        hMeta.value.codec,
        field[K](metaField) :: HNil,
        metaField :: HNil
      )(h => (metaField -> h) :: HNil)
    }

    implicit def baseOptionalField[K <: Symbol, H](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[MetaS[H]],
      c: Configuration
    ): Meta0.Aux[
      FieldType[K, Option[H]] :: HNil,
      Option[H],
      FieldType[K, OptionalMetaField[H]] :: HNil,
      OptionalMetaField[H] :: HNil,
      ExtractedOptionalField[H] :: HNil
    ] = {
      val fieldName: String = w.value.name

      val metaField: OptionalMetaField[H] = new MetaField[H] {
        override val sqlName: String = c.fieldFormatter.formatField(fieldName)
        override val name: String    = fieldName
        override val codec: Codec[H] = hMeta.value.codec
      }.opt

      Meta0[
        FieldType[K, Option[H]] :: HNil,
        Option[H],
        FieldType[K, OptionalMetaField[H]] :: HNil,
        OptionalMetaField[H] :: HNil,
        ExtractedOptionalField[H] :: HNil
      ](
        metaField.codec,
        field[K](metaField) :: HNil,
        metaField :: HNil
      )(h => (metaField -> h) :: HNil)
    }

    @nowarn("cat=unused")
    implicit def baseMeta[K <: Symbol, H, HF <: HList, HMF <: HList, HEF <: HList](implicit
      w: Witness.Aux[K],
      hMeta: Lazy[Meta.Aux[H, HF, HMF, HEF]],
      l: Last[HF], // Check HF is not empty
      evNotOption: H <:!< Option[_]
    ): Meta0.Aux[
      FieldType[K, H] :: HNil,
      H,
      FieldType[K, Meta.Aux[H, HF, HMF, HEF]] :: HNil,
      HMF,
      ExtractedMeta[H, HF, HMF, HEF] :: HNil
    ] = {
      val meta = hMeta.value

      Meta0(
        meta.codec,
        field[K](meta) :: HNil,
        meta.metaFields
      )(h => (meta -> h) :: HNil)
    }

    @nowarn("cat=unused")
    implicit def baseOptionalMeta[
      K <: Symbol,
      H,
      HMF <: HList,
      HEF <: HList,
      IHF <: HList,
      IHMF <: HList,
      IHEF <: HList
    ](implicit
      w: Witness.Aux[K],
      hOptionMeta: Lazy[OptionalMeta.Aux[H, HMF, HEF, IHF, IHMF, IHEF]]
    ): Meta0.Aux[
      FieldType[K, Option[H]] :: HNil,
      Option[H],
      FieldType[K, OptionalMeta.Aux[H, HMF, HEF, IHF, IHMF, IHEF]] :: HNil,
      HMF,
      ExtractedOptionalMeta[H, HMF, HEF, IHF, IHMF, IHEF] :: HNil
    ] = {
      val optionMeta = hOptionMeta.value

      Meta0[
        FieldType[K, Option[H]] :: HNil,
        Option[H],
        FieldType[K, OptionalMeta.Aux[H, HMF, HEF, IHF, IHMF, IHEF]] :: HNil,
        HMF,
        ExtractedOptionalMeta[H, HMF, HEF, IHF, IHMF, IHEF] :: HNil
      ](
        optionMeta.codec,
        field[K](optionMeta) :: HNil,
        optionMeta.metaFields
      )(h => (optionMeta -> h) :: HNil)
    }

    @nowarn("cat=unused")
    implicit def hlistField[
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
      evNotOption: L <:!< Option[_],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF, BMF, BEF],
      lMeta: Lazy[MetaS[L]],
      fPrepend: Prepend.Aux[BF, FieldType[K, MetaField[L]] :: HNil, AF],
      mfPrepend: Prepend.Aux[BMF, MetaField[L] :: HNil, AMF],
      efPrepend: Prepend.Aux[BEF, ExtractedField[L] :: HNil, AEF],
      c: Configuration
    ): Meta0.Aux[A, BM ~ L, AF, AMF, AEF] = {
      val fieldName: String = w.value.name

      // Simple type
      val metaField =
        new MetaField[L] {
          override def sqlName: String = c.fieldFormatter.formatField(fieldName)

          override def name: String = fieldName

          override def codec: Codec[L] = lMeta.value.codec
        }

      Meta0(
        previous.meta.codec ~ metaField.codec,
        previous.meta.fields ::: (field[K](metaField) :: HNil),
        previous.meta.metaFields ::: (metaField :: HNil)
      ) { case (b, l) =>
        previous.meta.extract(b) ::: (metaField -> l :: HNil)
      }
    }

    @nowarn("cat=unused")
    implicit def hlistOptionalField[
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
      ev: LFT =:= FieldType[K, Option[L]],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF, BMF, BEF],
      lMeta: Lazy[MetaS[L]],
      fPrepend: Prepend.Aux[BF, FieldType[K, OptionalMetaField[L]] :: HNil, AF],
      mfPrepend: Prepend.Aux[BMF, OptionalMetaField[L] :: HNil, AMF],
      efPrepend: Prepend.Aux[BEF, ExtractedOptionalField[L] :: HNil, AEF],
      c: Configuration
    ): Meta0.Aux[A, BM ~ Option[L], AF, AMF, AEF] = {
      val fieldName: String = w.value.name

      // Simple type
      val metaField: OptionalMetaField[L] =
        new MetaField[L] {
          override def sqlName: String = c.fieldFormatter.formatField(fieldName)

          override def name: String = fieldName

          override def codec: Codec[L] = lMeta.value.codec
        }.opt

      Meta0[A, BM ~ Option[L], AF, AMF, AEF](
        previous.meta.codec ~ metaField.codec,
        previous.meta.fields ::: (field[K](metaField) :: HNil),
        previous.meta.metaFields ::: (metaField :: HNil)
      ) { case (b, l) =>
        previous.meta.extract(b) ::: (metaField -> l :: HNil)
      }
    }

    @nowarn("cat=unused")
    implicit def hlistMeta[
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
      evNotOption: L <:!< Option[_],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF, BMF, BEF],
      lMeta: Lazy[Meta.Aux[L, LF, LMF, LEF]],
      nonEmptyLF: Last[LF],
      fPrepend: Prepend.Aux[BF, FieldType[K, Meta.Aux[L, LF, LMF, LEF]] :: HNil, AF],
      mfPrepend: Prepend.Aux[BMF, LMF, AMF],
      efPrepend: Prepend.Aux[BEF, ExtractedMeta[L, LF, LMF, LEF] :: HNil, AEF]
    ): Meta0.Aux[A, BM ~ L, AF, AMF, AEF] = {
      val meta: Meta.Aux[L, LF, LMF, LEF]                  = lMeta.value
      val metaElt: FieldType[K, Meta.Aux[L, LF, LMF, LEF]] = field[K](meta)
      val codec: Codec[BM ~ L]                             = previous.meta.codec ~ meta.codec

      Meta0(
        codec,
        previous.meta.fields ::: (metaElt :: HNil),
        previous.meta.metaFields ::: meta.metaFields
      ) { case (b, l) =>
        previous.meta.extract(b) ::: (meta -> l) :: HNil
      }
    }

    @nowarn("cat=unused")
    implicit def hlistOptionalMeta[
      A <: HList,    // The object Generic Representation
      AF <: HList,   // Fields of the object
      AMF <: HList,  // MetaFields of the object
      AEF <: HList,  // Extracted field type of the object
      B <: HList,    // The previous elements of A without L
      BM,            // Meta type of the previous elements
      BF <: HList,   // Fields of the previous elements
      BMF <: HList,  // MetaFields of the previous elements
      BEF <: HList,  // Extracted fields of the previous elements
      LFT,           // Last element FieldType
      L,             // Last element type (without option)
      LMF <: HList,  // MetaFields of the last element HNil in this case
      LEF <: HList,  // Extracted field type of the last element
      LIF <: HList,  // Fields of the internal object
      LIMF <: HList, // MetaFields of the internal object
      LIEF <: HList, // Extracted fields of the internal object
      K <: Symbol    // FieldName of the last element
    ](implicit
      init: Init.Aux[A, B],
      last: Last.Aux[A, LFT],
      aPrepend: Prepend.Aux[B, LFT :: HNil, A],
      ev: LFT =:= FieldType[K, Option[L]],
      w: Witness.Aux[K],
      previous: Meta0.Aux[B, BM, BF, BMF, BEF],
      lMeta: Lazy[OptionalMeta.Aux[L, LMF, LEF, LIF, LIMF, LIEF]],
      fPrepend: Prepend.Aux[BF, FieldType[K, OptionalMeta.Aux[L, LMF, LEF, LIF, LIMF, LIEF]] :: HNil, AF],
      mfPrepend: Prepend.Aux[BMF, LMF, AMF],
      efPrepend: Prepend.Aux[BEF, ExtractedOptionalMeta[L, LMF, LEF, LIF, LIMF, LIEF] :: HNil, AEF]
    ): Meta0.Aux[A, BM ~ Option[L], AF, AMF, AEF] = {
      val meta: OptionalMeta.Aux[L, LMF, LEF, LIF, LIMF, LIEF]                  = lMeta.value
      val metaElt: FieldType[K, OptionalMeta.Aux[L, LMF, LEF, LIF, LIMF, LIEF]] = field[K](meta)
      val codec: Codec[BM ~ Option[L]]                                          = previous.meta.codec ~ meta.codec

      Meta0(
        codec,
        previous.meta.fields ::: (metaElt :: HNil),
        previous.meta.metaFields ::: meta.metaFields
      ) { case (b, l) =>
        previous.meta.extract(b) ::: ((meta -> l) :: HNil)
      }
    }
  }

}
