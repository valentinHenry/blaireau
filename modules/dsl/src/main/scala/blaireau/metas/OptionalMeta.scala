// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.metas.Meta.Aux
import shapeless.{HList, LabelledGeneric, Lazy}
import skunk.Codec
import skunk.util.Twiddler

import scala.annotation.nowarn

trait OptionalMeta[A] extends FieldProduct { self =>
  override type T = Option[A]

  type EF <: HList // Extracted Fields ex: (MetaField[F1] -> F1) :: (MetaField[F2] -> F2) :: ... :: HNil
  type MF <: HList

  type IF <: HList
  type IEF <: HList
  type IMF <: HList

  def internal: Meta.Aux[A, IF, IEF, IMF]

  def codec: Codec[Option[A]]

  private[blaireau] def metaFields: MF // fields in the sql

  override def toString: String = s"OptionalMeta($codec, $metaFields)"

  private[blaireau] def extract(t: T): EF

  def imap[B](f: A => B)(g: B => A): OptionalMeta.Aux[B, MF, EF, IF, IMF, IEF] =
    new OptionalMeta[B] {
      override final type T = Option[B]

      override final type MF = self.MF
      override final type EF = self.EF

      override final type IF  = self.IF
      override final type IMF = self.IMF
      override final type IEF = self.IEF

      override def internal: Aux[B, IF, IEF, IMF]              = self.internal.imap(f)(g)
      override def codec: Codec[Option[B]]                     = self.codec.imap(_.map(f))(_.map(g))
      override private[blaireau] def metaFields: MF            = self.metaFields
      override private[blaireau] def extract(t: Option[B]): EF = self.extract(t.map(g))
    }

  def gmap[B](implicit twiddler: Twiddler.Aux[B, A]): OptionalMeta.Aux[B, MF, EF, IF, IMF, IEF] =
    imap(twiddler.from)(twiddler.to)
}

object OptionalMeta {
  type Aux[T, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList] = OptionalMeta[T] {
    type MF = MF0
    type EF = EF0

    type IF  = IF0
    type IMF = IMF0
    type IEF = IEF0
  }

  def apply[T0, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList](
    _internal: Meta.Aux[T0, IF0, IEF0, IMF0],
    _codec: Codec[Option[T0]],
    _metaFields: MF0,
    _extract: Option[T0] => EF0
  ): OptionalMeta.Aux[T0, MF0, EF0, IF0, IMF0, IEF0] = new OptionalMeta[T0] {
    override final type T = Option[T0]

    override final type MF = MF0
    override final type EF = EF0

    override final type IF  = IF0
    override final type IMF = IMF0
    override final type IEF = IEF0

    override def internal: Meta.Aux[T0, IF0, IEF0, IMF0] = _internal
    override def codec: Codec[Option[T0]]                = _codec
    override private[blaireau] def metaFields: MF        = _metaFields
    override private[blaireau] def extract(t: T): EF     = _extract(t)
  }

  @nowarn
  implicit def generic[A, O, T, MF <: HList, EF <: HList, IF <: HList, IMF <: HList, IEF <: HList](implicit
    generic: LabelledGeneric.Aux[A, O],
    meta0: Lazy[OptionalMeta0.Aux[O, T, MF, EF, IF, IMF, IEF]],
    tw: Twiddler.Aux[A, T]
  ): OptionalMeta.Aux[A, MF, EF, IF, IMF, IEF] =
    meta0.value.optionalMeta.gmap

  trait OptionalMeta0[O] {
    type T
    type MF <: HList
    type EF <: HList
    type IF <: HList
    type IMF <: HList
    type IEF <: HList

    def optionalMeta: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF]
  }

  object OptionalMeta0 {
    type Aux[O, T0, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList] = OptionalMeta0[O] {
      type T   = T0
      type MF  = MF0
      type EF  = EF0
      type IF  = IF0
      type IMF = IMF0
      type IEF = IEF0
    }

    def apply[O, T0, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList](
      _optionalMeta: OptionalMeta.Aux[T0, MF0, EF0, IF0, IMF0, IEF0]
    ): OptionalMeta0.Aux[O, T0, MF0, EF0, IF0, IMF0, IEF0] =
      new OptionalMeta0[O] {
        override final type T   = T0
        override final type MF  = MF0
        override final type EF  = EF0
        override final type IF  = IF0
        override final type IMF = IMF0
        override final type IEF = IEF0

        override val optionalMeta: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF] = _optionalMeta
      }

//    implicit def base[K <: Symbol, H](implicit
//      w: Witness.Aux[K],
//      hMeta: Lazy[Meta.Aux[H, HNil, HNil, HNil]]
//    ): OptionalMeta0.Aux[
//      FieldType[K, H] :: HNil,            // O
//      H,                                  // T
//      FieldType[K, MetaField[Option[H]]] :: HNil, // MF
//      ExtractedField[H] :: HNil,          // IF
//      FieldType[K, MetaField[H]] :: HNil, // EF
//      HNil,                               // IMF
//      HNil                                // IEF
//    ] = {
//      val fieldName: String = w.value.name
//
//      val metaField: FieldType[K, MetaField[H]] = field[K](
//        new MetaField[H] {
//          override val sqlName: String = fieldName
//          override val name: String    = fieldName
//          override val codec: Codec[H] = hMeta.value.codec
//        }
//      )
//
//      Meta0(
//        Meta(hMeta.value.codec, metaField :: HNil, metaField :: HNil)(h => (metaField -> h) :: HNil)
//      )
//    }
  }
}
