// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic

import blaireau.{BlaireauConfiguration, Meta, MetaField}
import magnolia.{CaseClass, Param, SealedTrait}
import skunk._
import skunk.util.Twiddler

import scala.language.experimental.macros
import shapeless.{Generic, HList}

//TODO find a way to make it type safe...
private[generic] object MagnoliaMeta {
  trait MetaAcc {
    type CurrType

    type CodecTwiddlerT
    type TList <: HList

    def codec: Codec[CodecTwiddlerT]

    def twiddler: Twiddler.Aux[TList, CodecTwiddlerT]
  }

  object MetaAcc {
    import shapeless._
    def first[T](firstParam: Param[Meta, T]): MetaAcc =
      new MetaAcc {
        override type CurrType = firstParam.PType

        override type TList          = CurrType :: HNil
        override type CodecTwiddlerT = firstParam.PType

        override def codec: Codec[CodecTwiddlerT] = firstParam.typeclass.codec

        override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = Twiddler.base
      }

    def make[T](metaAcc: MetaAcc, curr: Param[Meta, T]): MetaAcc =
      new MetaAcc {
        override type CurrType = curr.PType
        override type TList    = CurrType :: metaAcc.TList

        override type CodecTwiddlerT = metaAcc.CodecTwiddlerT ~ curr.PType

        override def codec: Codec[CodecTwiddlerT] = metaAcc.codec ~ curr.typeclass.codec

        override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = new Twiddler[TList] {
          override type Out = CodecTwiddlerT

          override def to(h: TList): CodecTwiddlerT =
            (metaAcc.twiddler.to(h.tail), h.head)

          override def from(o: CodecTwiddlerT): TList =
            o._2 :: metaAcc.twiddler.from(o._1)
        }
      }
  }

  def combine[T: Generic](ctx: CaseClass[Meta, T])(implicit config: BlaireauConfiguration): Meta[T] =
    ctx.parameters.toList match {
      case Nil =>
        Meta(Void.codec.imap(_ => ctx.rawConstruct(Seq.empty[Any]))(_ => Void), Nil)

      case param :: Nil if ctx.isValueClass =>
        val codec = param.typeclass.codec.imap[T](p => ctx.rawConstruct(Seq(p)))(cc => param.dereference(cc))
        Meta(codec, Nil)

      case firstParam :: tail =>
        val formatter = config.formatter

        def makeField(p: Param[Meta, T]): MetaField = new MetaField {
          override type T = p.PType

          override def name: String = formatter(p.label)
        }

        val firstField: List[MetaField] =
          if (firstParam.typeclass.fields.isEmpty) makeField(firstParam) :: Nil else firstParam.typeclass.fields

        val firstMetaAcc: MetaAcc = MetaAcc.first(firstParam)

        val (params, metaAcc) = tail.foldLeft((firstField, firstMetaAcc)) { case ((fields, metaAcc), curr) =>
          val currMeta: Meta[curr.PType] = curr.typeclass
          val currMetaAcc: MetaAcc       = MetaAcc.make(metaAcc, curr)

          if (currMeta.fields.isEmpty) (fields :+ makeField(curr), currMetaAcc)
          else (fields ::: currMeta.fields, currMetaAcc)
        }

        val generic = Generic[T]

        // FIXME this part is not type safe!!!
        val codec = metaAcc.codec.imap[T] { o =>
          // Looks bad
          val tList        = metaAcc.twiddler.from(o)
          val genericTList = HList.unsafeReverse(tList).asInstanceOf[generic.Repr]
          generic.from(genericTList)
        } { i =>
          // Looks worse
          val genericTList = generic.to(i).asInstanceOf[HList]
          val tList        = HList.unsafeReverse(genericTList).asInstanceOf[metaAcc.TList]
          metaAcc.twiddler.to(tList)
        }

        Meta(codec, params)
    }

  def dispatch[T](ctx: SealedTrait[Meta, T]): Meta[T] =
    throw new UnsupportedOperationException("ADTs are not supported at the moment")
}
