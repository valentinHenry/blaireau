package blaireau.dsl

import blaireau.{ExportedMeta, Meta, MetaField}
import cats.data.State
import skunk.{Codec, Decoder, Encoder}
import cats.syntax.all._
import shapeless.Witness.Aux

import scala.language.implicitConversions
import scala.reflect.runtime.universe.reify
import shapeless.labelled._
import shapeless.ops.hlist.{Last, Prepend}
import shapeless.{::, Generic, HList, HNil, LabelledGeneric, Lazy, Witness, the}
import skunk.util._
import skunk.Void
import shapeless.record._
import skunk.data.Type

object DSLSpec extends App {

  final case class Test1(oui: String)

  final case class Test2(non: Int, ah: String)

  def mergeCodecs[H, T <: HList](hCodec: Codec[H], tCodec: Codec[T]): Codec[H :: T] = new Codec[H :: T] {
    private val pe = new Encoder[H :: T] {
      override def encode(a: H :: T): List[Option[String]] = hCodec.encode(a.head) ++ tCodec.encode(a.tail)

      override val types: List[Type]       = hCodec.types ++ tCodec.types
      override val sql: State[Int, String] = (hCodec.sql, tCodec.sql).mapN((a, b) => s"$a, $b")
    }

    private val pd = new Decoder[H :: T] {
      override val types: List[Type] = hCodec.types ++ tCodec.types

      override def decode(offset: Int, ss: List[Option[String]]): Either[Decoder.Error, H :: T] = {
        val (sa, sb) = ss.splitAt(hCodec.types.length)
        (hCodec.decode(offset, sa), tCodec.decode(offset + hCodec.length, sb)).mapN(_ :: _)
      }
    }

    override def encode(ab: H :: T): List[Option[String]] = pe.encode(ab)

    override def decode(offset: Int, ss: List[Option[String]]): Either[Decoder.Error, H :: T] =
      pd.decode(offset, ss)

    override val sql: State[Int, String] = (hCodec.sql, tCodec.sql).mapN((a, b) => s"$a, $b")
    override val types: List[Type]       = hCodec.types ++ tCodec.types
  }

  import blaireau.generic.codec.instances.all._

//  object CodecDerivation {
//    //TODO
//    implicit val hnilCodec: Codec[HNil] = Void.codec.imap(_ => ???)(jsp => Void)
//
//    implicit def hlistCodec[H, T <: HList](implicit hCodec: Lazy[Codec[H]], tCodec: Codec[T]): Codec[H :: T] =
//      mergeCodecs(hCodec.value, tCodec)
//
//    implicit final def genericCodecEncoder[A, H](implicit
//      generic: Generic.Aux[A, H],
//      hMeta: Lazy[Codec[H]]
//    ): Codec[A] =
//      hMeta.value.imap(generic.from)(generic.to)
//
//    val testCodec: Codec[Int :: String :: HNil] = implicitly
//    println(testCodec)
//
//    val testCodec1: Codec[Test1] = implicitly
//    println(testCodec1)
//
//    val testCodec2: Codec[Test2] = implicitly
//    println(testCodec2)
//
//  }
//
  // METAS
  object MetaDerivation {
    implicit val hnilMeta: Meta.Aux[HNil, HNil] = Meta[HNil, HNil](
      Void.codec.imap(_ => ???)(_ => Void), //FIXME
      List.empty,
      HNil
    )

    implicit val hnilExportedMeta: ExportedMeta.Aux[HNil, HNil, HNil] = ExportedMeta(hnilMeta)

    implicit final def optionMeta[T, F <: HList](implicit m: Meta.Aux[T, F]): Meta.Aux[Option[T], F] =
      Meta[Option[T], F](m.codec.opt, m.fieldNames, m.metaFields)

    implicit final def codecToMeta[T](implicit c: Codec[T]): Meta.Aux[T, HNil] = Meta[T, HNil](c, Nil, HNil)

    /*
    implicit final def hlistMetaSimple[
      K <: Symbol,
      H,
      TE <: HList,
      TM <: HList,
      TF <: HList
    ](implicit
      witness: Witness.Aux[K],
      lHMeta: Lazy[Meta.Aux[H, HNil]],
      tMeta: Meta.Aux[TM, TF]
    ): Meta.Aux[H :: TM, FieldType[K, MetaField[H]] :: TF] = {
      val fieldName: String = witness.value.name
      val hMeta             = lHMeta.value
      val hCodec            = hMeta.codec
      val tCodec            = tMeta.codec

      // Simple type
      val metaField: FieldType[K, MetaField[H]] = field[K](
        new MetaField[H] {
          override def sqlName: String = fieldName

          override def name: String = fieldName

          override def codec: Codec[H] = hCodec
        }
      )

      val codec = mergeCodecs(hMeta.codec, tCodec)

      Meta(
        c = codec,
        f = fieldName :: tMeta.fieldNames,
        mf = metaField :: tMeta.metaFields
      )
    }
     */

    implicit final def hlistExportedMetaSimple[
      K <: Symbol,
      H,
      TE <: HList,
      TM <: HList,
      TF <: HList
    ](implicit
      witness: Witness.Aux[K],
      lHMeta: Lazy[Meta.Aux[H, HNil]],
      tEMeta: ExportedMeta.Aux[TE, TM, TF]
    ): ExportedMeta.Aux[FieldType[K, H] :: TE, H :: TM, FieldType[K, MetaField[H]] :: TF] = {
      val fieldName: String = witness.value.name
      val hMeta             = lHMeta.value
      val hCodec            = hMeta.codec
      val tCodec            = tEMeta.meta.codec

      // Simple type
      val metaField: FieldType[K, MetaField[H]] = field[K](
        new MetaField[H] {
          override def sqlName: String = fieldName

          override def name: String = fieldName

          override def codec: Codec[H] = hCodec
        }
      )

      val codec = mergeCodecs(hMeta.codec, tCodec)

      ExportedMeta(
        Meta(
          c = codec,
          f = fieldName :: tEMeta.meta.fieldNames,
          mf = metaField :: tEMeta.meta.metaFields
        )
      )
    }

    implicit final def hlistExportedMetaCompound[
      K <: Symbol,
      H,
      HF <: HList,
      TE <: HList,
      TM <: HList,
      TF <: HList,
      POut <: HList
    ](implicit
      hMeta: Lazy[Meta.Aux[H, HF]],
      hfLast: Last[HF], // Since last is found, hMeta has to be with at least one internal type
      tMeta: Meta.Aux[TM, TF],
      p: Prepend.Aux[HF, TF, POut]
    ): ExportedMeta.Aux[FieldType[K, H] :: TE, H :: TM, POut] = {
      val headMeta = hMeta.value

      val codec = mergeCodecs(headMeta.codec, tMeta.codec)

      ExportedMeta(
        Meta[H :: TM, POut](
          c = codec,
          f = headMeta.fieldNames ::: tMeta.fieldNames,
          mf = headMeta.metaFields ::: tMeta.metaFields
        )
      )
    }

    implicit final def genericExportedMeta[AG, RE, RM, RF <: HList](implicit
      lgeneric: LabelledGeneric.Aux[AG, RE],
      hMeta: Lazy[ExportedMeta.Aux[RE, RM, RF]]
    ): ExportedMeta.Aux[AG, RM, RF] =
      ExportedMeta(hMeta.value.meta)
  }
  import MetaDerivation._

  final case class IDK(yes: String, no: Int, maybe: Float)
  final case class Hmm(idk: IDK, crap: Double)

  type TestT = FieldType["ok", Int] :: HNil

  import shapeless.syntax.singleton._
  type K = Symbol with 1
  type L = Symbol with 2
  type M = Symbol with 3
  implicit val kw: Aux[K] = Witness.mkWitness[K](Symbol("one").asInstanceOf[Symbol with 1])
  implicit val lw: Aux[L] = Witness.mkWitness[L](Symbol("two").asInstanceOf[Symbol with 2])
  implicit val mw: Aux[M] = Witness.mkWitness[M](Symbol("three").asInstanceOf[Symbol with 3])

  type TypeList = FieldType[K, String] :: FieldType[L, Int] :: FieldType[M, Long] :: HNil

  val exportedTest = the[ExportedMeta[TypeList]]

  val exportedIDK = the[ExportedMeta[IDK]]
  val idkMeta     = exportedIDK.meta
  val idkFields   = idkMeta.metaFields

//  def fieldRetriever[A](name: String): MetaField[A] =
//    idkFields.get(Symbol(name))

  val pray: MetaField[String] = idkFields.get(Symbol("yes"))
  println(pray)
  println(idkFields)
  println(exportedIDK.meta.fieldNames)

//  val exportedHmm = the[ExportedMeta[Hmm]]
//  println(exportedHmm.meta.fieldNames)

}
