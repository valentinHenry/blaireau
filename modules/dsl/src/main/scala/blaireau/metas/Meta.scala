// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import shapeless.labelled.{FieldType, field}
import shapeless.ops.hlist.{Last, Prepend}
import shapeless.ops.record.Selector
import shapeless.tag.@@
import shapeless.{::, HList, HNil, LabelledGeneric, LabelledMacros, Lazy, Witness}
import skunk.{Codec, ~}

import scala.language.experimental.macros
import scala.language.{dynamics, implicitConversions}

trait MetaField[H] {
  private[blaireau] def sqlName: String
  private[blaireau] def name: String
  private[blaireau] def codec: Codec[H]

  override def toString: String = s"MetaField($sqlName:$name:$codec)"
}

trait Meta[A] extends TableSchema[A] { self =>
  type F <: HList

  def codec: Codec[A]
  def metaFields: F

  def imap[B](f: A => B)(g: B => A): Meta.Aux[B, F] =
    new Meta[B] {
      type F = self.F
      def codec: Codec[B] = self.codec.imap(f)(g)
      def metaFields: F   = self.metaFields
    }

  override def toString: String = s"Meta($codec)"
}

object Meta {
  type Aux[T, FieldsT <: HList] = Meta[T] { type F = FieldsT }

  def of[T](c: Codec[T]): Meta.Aux[T, HNil] = Meta[T, HNil](c, HNil)

  def apply[T, FieldsT <: HList](c: Codec[T], mf: FieldsT): Meta.Aux[T, FieldsT] = new Meta[T] {
    override type F = FieldsT
    override def codec: Codec[T]     = c
    override def metaFields: FieldsT = mf
  }
}

trait TableSchema[T] extends Dynamic { self: Meta[T] =>
  import shapeless.record._
  type F <: HList

  def column(k: Witness)(implicit s: Selector[F, k.T]): s.Out = metaFields.get(k)
  def apply(k: Witness)(implicit s: Selector[F, k.T]): s.Out  = metaFields.get(k)

  // TODO macro: replace select dynamic by functions of the present fields (to help idea + the user)
  def selectDynamic(k: String)(implicit s: Selector[F, Symbol @@ k.type]): s.Out = metaFields.record.selectDynamic(k)
}

object TableSchema {
  type Aux[T, FT <: HList] = TableSchema[T] { type F = FT }
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

  def apply[T](implicit e: ExportedMeta[T]): ExportedMeta.Aux[T, e.MetaT, e.MetaF] = e

  implicit final def single[K <: Symbol, H](implicit
    witness: Witness.Aux[K],
    lHMeta: Lazy[Meta.Aux[H, HNil]]
  ): ExportedMeta.Aux[FieldType[K, H] :: HNil, H, FieldType[K, MetaField[H]] :: HNil] = {
    val fieldName: String = witness.value.name
    val hMeta             = lHMeta.value

    val metaField: FieldType[K, MetaField[H]] = field[K](
      new MetaField[H] {
        override def sqlName: String = fieldName
        override def name: String    = fieldName
        override def codec: Codec[H] = hMeta.codec
      }
    )

    ExportedMeta(
      Meta[H, FieldType[K, MetaField[H]] :: HNil](
        c = hMeta.codec,
        mf = metaField :: HNil
      )
    )
  }

  implicit final def simple[
    K <: Symbol,
    H,
    TE <: HList,
    TM,
    TF <: HList
  ](implicit
    witness: Witness.Aux[K],
    lHMeta: Lazy[Meta.Aux[H, HNil]],
    tEMeta: ExportedMeta.Aux[TE, TM, TF]
  ): ExportedMeta.Aux[FieldType[K, H] :: TE, H ~ TM, FieldType[K, MetaField[H]] :: TF] = {
    val fieldName: String = witness.value.name
    val hMeta             = lHMeta.value
    val hCodec            = hMeta.codec
    val tCodec            = tEMeta.meta.codec

    // Simple type
    val metaField: FieldType[K, MetaField[H]] = field[K](
      new MetaField[H] {
        override def sqlName: String = fieldName
        override def name: String    = fieldName
        override def codec: Codec[H] = hCodec
      }
    )

    val codec = hMeta.codec ~ tCodec

    ExportedMeta(
      Meta(
        c = codec,
        mf = metaField :: tEMeta.meta.metaFields
      )
    )
  }

  implicit final def compound[
    K <: Symbol,
    H,
    HF <: HList,
    TE <: HList,
    TM,
    TF <: HList,
    OutF <: HList
  ](implicit
    hMeta: Lazy[Meta.Aux[H, HF]],
    hfLast: Last[HF], // Since last is found, hMeta has to be with at least one internal type
    tMeta: ExportedMeta.Aux[TE, TM, TF],
    p: Prepend.Aux[HF, TF, OutF]
  ): ExportedMeta.Aux[FieldType[K, H] :: TE, H ~ TM, OutF] = {
    val headMeta = hMeta.value

    val codec = headMeta.codec ~ tMeta.meta.codec

    ExportedMeta(
      Meta[H ~ TM, OutF](
        c = codec,
        mf = headMeta.metaFields ::: tMeta.meta.metaFields
      )
    )
  }

  implicit final def generic[AG, RE, RM, RF <: HList](implicit
    lgeneric: LabelledGeneric.Aux[AG, RE],
    hMeta: Lazy[ExportedMeta.Aux[RE, RM, RF]]
  ): ExportedMeta.Aux[AG, RM, RF] =
    ExportedMeta(hMeta.value.meta)
}
