// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import shapeless.ops.hlist.{Last, Mapper}
import shapeless.{HList, HNil, Lazy, Poly1}
import skunk.Codec
import skunk.util.Twiddler

import scala.annotation.nowarn

trait OptionalMeta[A] extends Meta[Option[A]] { self =>
  override type T = Option[A]

  type F = HNil
  type MF <: HList
  type EF <: HList // Extracted Fields ex: (MetaField[F1] -> F1) :: (MetaField[F2] -> F2) :: ... :: HNil
  type OEF = EF

  type IF <: HList
  type IEF <: HList
  type IMF <: HList

  private[blaireau] override final def fields: HNil = HNil

  def internal: Meta.Aux[A, IF, IMF, IEF, EF]

  def codec: Codec[Option[A]]

  private[blaireau] def metaFields: MF // fields in the sql

  override def toString: String = s"OptionalMeta($codec, $metaFields)"

  private[blaireau] def extract(t: Option[A]): EF

  private[blaireau] override def extractOpt(t: Option[Option[A]]): OEF = extract(t.flatten)

  def imap[B](f: A => B)(g: B => A): OptionalMeta.Aux[B, MF, EF, IF, IMF, IEF] =
    new OptionalMeta[B] {
      override final type T   = Option[B]
      override final type F   = HNil
      override final type MF  = self.MF
      override final type EF  = self.EF
      override final type OEF = EF

      override final type IF  = self.IF
      override final type IMF = self.IMF
      override final type IEF = self.IEF

      override def internal: Meta.Aux[B, IF, IMF, IEF, EF]     = self.internal.imap(f)(g)
      override def codec: Codec[Option[B]]                     = self.codec.imap(_.map(f))(_.map(g))
      override private[blaireau] def metaFields: MF            = self.metaFields
      override private[blaireau] def extract(t: Option[B]): EF = self.extract(t.map(g))
    }

  def gmap[B](implicit twiddler: Twiddler.Aux[B, A]): OptionalMeta.Aux[B, MF, EF, IF, IMF, IEF] =
    imap(twiddler.from(_))(twiddler.to(_))
}

object OptionalMeta {
  type Aux[T, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList] =
    OptionalMeta[T] {
      type MF = MF0
      type EF = EF0

      type IF  = IF0
      type IMF = IMF0
      type IEF = IEF0
    }

  def apply[T0, MF0 <: HList, EF0 <: HList, IF0 <: HList, IMF0 <: HList, IEF0 <: HList](
    _internal: Meta.Aux[T0, IF0, IMF0, IEF0, EF0],
    _codec: Codec[Option[T0]],
    _metaFields: MF0
  )(
    _extract: Option[T0] => EF0
  ): OptionalMeta.Aux[T0, MF0, EF0, IF0, IMF0, IEF0] = new OptionalMeta[T0] {
    override final type T  = Option[T0]
    override final type F  = HNil
    override final type MF = MF0
    override final type EF = EF0

    override final type IF  = IF0
    override final type IMF = IMF0
    override final type IEF = IEF0

    override def internal: Meta.Aux[T0, IF0, IMF0, IEF0, EF] = _internal
    override def codec: Codec[Option[T0]]                    = _codec
    override private[blaireau] def metaFields: MF            = _metaFields
    override private[blaireau] def extract(t: T): EF         = _extract(t)
  }

  @nowarn
  implicit def derive[T, MF <: HList, IF <: HList, IMF <: HList, IEF <: HList, OEF <: HList](implicit
    lMeta: Lazy[Meta.Aux[T, IF, IMF, IEF, OEF]],
    last: Last[IF],
    mfMapper: Mapper.Aux[metaFieldOptionMapper.type, IMF, MF]
  ): OptionalMeta.Aux[T, MF, OEF, IF, IMF, IEF] = {
    val meta = lMeta.value

    OptionalMeta(
      _internal = meta,
      _codec = meta.codec.opt,
      _metaFields = meta.metaFields.map(metaFieldOptionMapper)
    )(meta.extractOpt)
  }

  object metaFieldOptionMapper extends Poly1 {
    implicit def field[A]: Case.Aux[MetaField[A], MetaField[Option[A]]]                 = at(_.opt)
    implicit def optionalField[A]: Case.Aux[OptionalMetaField[A], OptionalMetaField[A]] = at(identity)
  }
}
