// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.metas.Meta.Aux
import shapeless.ops.hlist.{Last, Mapper}
import shapeless.{HList, Lazy, Poly1}
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
  type IOEF <: HList

  def internal: Meta.Aux[A, IF, IMF, IEF, IOEF]

  def codec: Codec[Option[A]]

  private[blaireau] def metaFields: MF // fields in the sql

  override def toString: String = s"OptionalMeta($codec, $metaFields)"

  private[blaireau] def extract(t: T): EF

  def imap[B](f: A => B)(g: B => A): OptionalMeta.Aux[B, MF, EF, IF, IMF, IEF, IOEF] =
    new OptionalMeta[B] {
      override final type T = Option[B]

      override final type MF = self.MF
      override final type EF = self.EF

      override final type IF   = self.IF
      override final type IMF  = self.IMF
      override final type IEF  = self.IEF
      override final type IOEF = self.IOEF

      override def internal: Aux[B, IF, IMF, IEF, IOEF]        = self.internal.imap(f)(g)
      override def codec: Codec[Option[B]]                     = self.codec.imap(_.map(f))(_.map(g))
      override private[blaireau] def metaFields: MF            = self.metaFields
      override private[blaireau] def extract(t: Option[B]): EF = self.extract(t.map(g))
    }

  def gmap[B](implicit twiddler: Twiddler.Aux[B, A]): OptionalMeta.Aux[B, MF, EF, IF, IMF, IEF, IOEF] =
    imap(twiddler.from)(twiddler.to)
}

object OptionalMeta {
  type Aux[T, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList, IOEF0 <: HList] =
    OptionalMeta[T] {
      type MF = MF0
      type EF = EF0

      type IF   = IF0
      type IMF  = IMF0
      type IEF  = IEF0
      type IOEF = IOEF0
    }

  def apply[T0, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList, IOEF0 <: HList](
    _internal: Meta.Aux[T0, IF0, IMF0, IEF0, IOEF0],
    _codec: Codec[Option[T0]],
    _metaFields: MF0
  )(
    _extract: Option[T0] => EF0
  ): OptionalMeta.Aux[T0, MF0, EF0, IF0, IMF0, IEF0, IOEF0] = new OptionalMeta[T0] {
    override final type T = Option[T0]

    override final type MF = MF0
    override final type EF = EF0

    override final type IF   = IF0
    override final type IMF  = IMF0
    override final type IEF  = IEF0
    override final type IOEF = IOEF0

    override def internal: Meta.Aux[T0, IF0, IMF0, IEF0, IOEF0] = _internal
    override def codec: Codec[Option[T0]]                       = _codec
    override private[blaireau] def metaFields: MF               = _metaFields
    override private[blaireau] def extract(t: T): EF            = _extract(t)
  }

  @nowarn
  implicit def derive[T, MF <: HList, IF <: HList, IMF <: HList, IEF <: HList, OEF <: HList](implicit
    lMeta: Lazy[Meta.Aux[T, IF, IMF, IEF, OEF]],
    last: Last[IF],
    mfMapper: Mapper.Aux[metaFieldOptionMapper.type, IMF, MF]
  ): OptionalMeta.Aux[T, MF, OEF, IF, IMF, IEF, OEF] = {
    val meta = lMeta.value

    OptionalMeta(
      _internal = meta,
      _codec = meta.codec.opt,
      _metaFields = meta.metaFields.map(metaFieldOptionMapper)
    )(meta.extractOpt)
  }

  object metaFieldOptionMapper extends Poly1 {
    implicit def field[A]: Case.Aux[MetaField[A], MetaField[Option[A]]] = at(_.opt)
  }

//  trait OptionalMeta0[O] {
//    type T
//    type MF <: HList
//    type EF <: HList
//    type IF <: HList
//    type IMF <: HList
//    type IEF <: HList
//
//    def optionalMeta: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF]
//  }
//
//  object OptionalMeta0 {
//    type Aux[O, T0, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList] = OptionalMeta0[O] {
//      type T   = T0
//      type MF  = MF0
//      type EF  = EF0
//      type IF  = IF0
//      type IMF = IMF0
//      type IEF = IEF0
//    }
//
//    def apply[O, T0, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList](
//      _optionalMeta: OptionalMeta.Aux[T0, MF0, EF0, IF0, IMF0, IEF0]
//    ): OptionalMeta0.Aux[O, T0, MF0, EF0, IF0, IMF0, IEF0] =
//      new OptionalMeta0[O] {
//        override final type T   = T0
//        override final type MF  = MF0
//        override final type EF  = EF0
//        override final type IF  = IF0
//        override final type IMF = IMF0
//        override final type IEF = IEF0
//
//        override val optionalMeta: OptionalMeta.Aux[T, MF, EF, IF, IMF, IEF] = _optionalMeta
//      }
//
//    implicit def base[K <: Symbol, H](implicit
//      w: Witness.Aux[K],
//      hMeta: Lazy[MetaS[H]]
//    ): OptionalMeta0.Aux[
//      FieldType[K, H] :: HNil,            // O
//      H,                                  // T
//      MetaField[Option[H]] :: HNil,       // MF
//      ExtractedField[Option[H]] :: HNil,  // EF
//      FieldType[K, MetaField[H]] :: HNil, // IF
//      MetaField[H] :: HNil,               // IMF
//      ExtractedField[H] :: HNil           // IEF
//    ] = {
//      val fieldName: String = w.value.name
//
//      val metaField: MetaField[H] = new MetaField[H] {
//        override val sqlName: String = fieldName
//        override val name: String    = fieldName
//        override val codec: Codec[H] = hMeta.value.codec
//      }
//
//      val optionMetaField: MetaField[Option[H]] = metaField.opt
//
//      OptionalMeta0(
//        OptionalMeta(
//          hMeta.value,
//          hMeta.value.codec.opt,
//          optionMetaField :: HNil
//        )(b => (optionMetaField -> b) :: HNil)
//      )
//    }
//
//    @nowarn
//    implicit def baseCompound[
//      K <: Symbol,
//      H,
//      HMF <: HList,
//      HEF <: HList,
//      IF <: HList,
//      IMF <: HList,
//      IEF <: HList
//    ](implicit
//      w: Witness.Aux[K],
//      hMeta: Lazy[metas.OptionalMeta.Aux[H, HMF, HEF, IF, IMF, IEF]]
//    ): OptionalMeta0.Aux[
//      FieldType[K, H] :: HNil,                                  // O
//      H,                                                        // T
//      HMF,                                                      // MF
//      ExtractedOptionalMeta[H, HMF, HEF, IF, IMF, IEF] :: HNil, // EF FIXME ?
//      FieldType[K, Meta.Aux[H, IF, IMF, IEF]] :: HNil,          // IF
//      IMF,                                                      // IMF
//      ExtractedMeta[H, IF, IMF, IEF] :: HNil                    // IEF
//    ] = {
//      val optionalMeta = hMeta.value
//
//      OptionalMeta0(
//        OptionalMeta[
//          H,                                                        // T
//          HMF,                                                      // MF
//          ExtractedOptionalMeta[H, HMF, HEF, IF, IMF, IEF] :: HNil, // EF FIXME ?
//          FieldType[K, Meta.Aux[H, IF, IMF, IEF]] :: HNil,          // IF
//          IMF,                                                      // IMF
//          ExtractedMeta[H, IF, IMF, IEF] :: HNil                    // IEF
//        ](
//          _internal = optionalMeta.internal,
//          _codec = optionalMeta.codec,
//          _metaFields = optionalMeta.metaFields
//        )(o => optionalMeta -> o :: HNil)
//      )
//    }
//
//    @nowarn
//    implicit def baseOptionalCompound[
//      K <: Symbol,
//      H,
//      HMF <: HList,
//      HEF <: HList,
//      IHF <: HList,
//      IHMF <: HList,
//      IHEF <: HList
//    ](implicit
//      w: Witness.Aux[K],
//      hOptionMeta: Lazy[OptionalMeta.Aux[H, HMF, HEF, IHF, IHMF, IHEF]]
//    ): OptionalMeta0.Aux[                                                   // O T MF EF IF IMF IEF
//      FieldType[K, Option[H]] :: HNil,                                      // O
//      Option[H],                                                            // T
//      HMF,                                                                  // MF
//      ExtractedOptionalMeta[H, HMF, HEF, IHF, IHMF, IHEF] :: HNil,          // EF
//      FieldType[K, OptionalMeta.Aux[H, HMF, HEF, IHF, IHMF, IHEF]] :: HNil, // IF
//      HMF,                                                                  // IMF
//      ExtractedOptionalMeta[H, HMF, HEF, IHF, IHMF, IHEF] :: HNil           // IEF
//    ] = {
//      val optionMeta = hOptionMeta.value
//
//      OptionalMeta0(
//        OptionalMeta(
//          _internal = optionMeta.internal,
//          _codec = optionMeta.codec.opt,
//          _metaFields = optionMeta.metaFields
//        )(_extract = ???)
//      )
//    }
//  }
}
