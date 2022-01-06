// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.dsl.actions.OptionalTwiddler
import blaireau.metas.Meta.Meta0
import cats.implicits.catsSyntaxOptionId
import shapeless.labelled.{FieldType, field}
import shapeless.ops.hlist.{Mapper, ToTraversable}
import shapeless.{HList, LabelledGeneric, Lazy, Poly1}
import skunk.Codec

import java.util.UUID
import scala.annotation.nowarn

trait OptionalMeta[A] extends Meta[Option[A]] {
  self =>
  type F = IF
  type MF <: HList
  type EF <: HList // Extracted Fields ex: (MetaField[F1] -> F1) :: (MetaField[F2] -> F2) :: ... :: HNil

  type IF <: HList
  type IEF <: HList
  type IMF <: HList

  def internal: Meta.Aux[A, IF, IMF, IEF]

  def imap[B](f: A => B)(g: B => A): OptionalMeta.Aux[B, MF, EF, IF, IMF, IEF] =
    new OptionalMeta[B] {
      override final type F  = IF
      override final type MF = self.MF
      override final type EF = self.EF

      override final type IF  = self.IF
      override final type IMF = self.IMF
      override final type IEF = self.IEF

      override def fields: IF = self.fields

      override def internal: Meta.Aux[B, IF, IMF, IEF] = self.internal.imap(f)(g)

      override def codec: Codec[Option[B]] = self.codec.imap(_.map(f))(_.map(g))

      override private[blaireau] def metaFields: MF = self.metaFields

      override private[blaireau] def extract(t: Option[B]): EF = self.extract(t.map(g))

      override private[blaireau] def idMapping: Map[UUID, UUID] = self.idMapping
    }

  def codec: Codec[Option[A]]

  private[blaireau] def metaFields: MF // fields in the sql

  override def toString: String = s"OptionalMeta($codec, $metaFields)"

  private[blaireau] def extract(t: Option[A]): EF

  private[blaireau] override def fields: IF
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
    _internal: Meta.Aux[T0, IF0, IMF0, IEF0],
    _codec: Codec[Option[T0]],
    _metaFields: MF0,
    _idMapping: Map[UUID, UUID]
  )(
    _extract: Option[T0] => EF0
  ): OptionalMeta.Aux[T0, MF0, EF0, IF0, IMF0, IEF0] = new OptionalMeta[T0] {
    override final type F  = IF
    override final type MF = MF0
    override final type EF = EF0

    override final type IF  = IF0
    override final type IMF = IMF0
    override final type IEF = IEF0

    override final val internal: Meta.Aux[T0, IF0, IMF0, IEF0] = _internal

    override final val codec: Codec[Option[T0]] = _codec

    override private[blaireau] final val metaFields: MF = _metaFields

    override private[blaireau] final def extract(t: T): EF = _extract(t)

    override private[blaireau] final val fields: F = _internal.fields

    override private[blaireau] final val idMapping = _idMapping
  }

  @nowarn("cat=unused")
  implicit final def genericMetaEncoder[
    A,
    H <: HList,
    OH <: HList,
    CT,
    F <: HList,
    MF <: HList,
    EF <: HList,
    IF <: HList,
    IEF <: HList,
    IMF <: HList,
    MFID <: HList,
    IMFID <: HList
  ](implicit
    generic: LabelledGeneric.Aux[A, H],
    m: Mapper.Aux[fieldOptionMapper.type, H, OH],
    lOptionalMeta: Lazy[Meta0.Aux[OH, CT, F, MF, EF]],
    lInternalMeta: Lazy[Meta.Aux[A, IF, IMF, IEF]],
    tw: OptionalTwiddler.Aux[A, CT],
    idM: Mapper.Aux[toIdMapper.type, MF, MFID],
    tl: ToTraversable.Aux[MFID, List, UUID],
    iIdM: Mapper.Aux[toIdMapper.type, IMF, IMFID],
    iTl: ToTraversable.Aux[IMFID, List, UUID]
  ): OptionalMeta.Aux[A, MF, EF, IF, IMF, IEF] = {
    val internal: Meta.Aux[A, IF, IMF, IEF]      = lInternalMeta.value
    val optional: Meta.Aux[Option[A], F, MF, EF] = lOptionalMeta.value.meta.imap(tw.from)(tw.to)

    val iIds: List[UUID] = internal.metaFields.map(toIdMapper).toList
    val ids: List[UUID]  = optional.metaFields.map(toIdMapper).toList

    val idMapping = ids.zip(iIds).toMap

    OptionalMeta(
      internal,
      optional.codec,
      optional.metaFields,
      idMapping
    )(optional.extract)
  }
}

object metaFieldOptionMapper extends Poly1 {
  implicit def field[A]: Case.Aux[MetaField[A], MetaField[Option[A]]] = at(_.opt)

  implicit def optionalField[A]: Case.Aux[OptionalMetaField[A], OptionalMetaField[A]] = at(identity)
}

object fieldOptionMapper extends Poly1 {
  implicit def toOption[K, A]: Case.Aux[FieldType[K, A], FieldType[K, Option[A]]] =
    at(f => field[K](f.asInstanceOf[A].some))
}

object toIdMapper extends Poly1 {
  implicit def field[MF <: MetaField[_]]: Case.Aux[MF, UUID] = at(_.id)
}
