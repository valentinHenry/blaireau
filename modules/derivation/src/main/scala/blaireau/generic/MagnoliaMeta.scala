// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic

import blaireau.Meta
import magnolia.{CaseClass, SealedTrait}
import skunk._
import skunk.util.Twiddler
import shapeless._
import shapeless.HList

import scala.language.experimental.macros

//TODO find a way to make it type safe...
private[generic] object MagnoliaMeta {
  trait MetaAcc{
    type CurrType

    type CodecTwiddlerT
    type TList <: HList

    def codec: Codec[CodecTwiddlerT]

    def twiddler: Twiddler.Aux[TList, CodecTwiddlerT]
  }

  def combine[T: Generic](ctx: CaseClass[Meta, T]): Meta[T] = {
    if (ctx.isValueClass){
      val param = ctx.parameters.head
      val codec = param.typeclass.codec.imap[T](p => ctx.rawConstruct(Seq(p)))(cc => param.dereference(cc))
      Meta(codec, Nil)

    } else if (ctx.parameters.isEmpty){
      Meta(Void.codec.imap(_ => ctx.rawConstruct(Seq.empty[Any]))(_ => Void), Nil)

    } else {
      val parameters = ctx.parameters
      val firstParam = parameters.head
      val firstField = if (firstParam.typeclass.fields.isEmpty) firstParam.label :: Nil else firstParam.typeclass.fields
      val firstMetaAcc: MetaAcc = new MetaAcc {
        override type CurrType = firstParam.PType

        override type TList = CurrType :: HNil
        override type CodecTwiddlerT = firstParam.PType

        override def codec: Codec[CodecTwiddlerT] = firstParam.typeclass.codec

        override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = Twiddler.base
      }

      val (params, metaAcc) = parameters.tail.foldLeft((firstField, firstMetaAcc)){ case ((fields, metaAcc), curr) =>
        val currMeta = curr.typeclass
        val currMetaAux: MetaAcc = new MetaAcc {
          override type CurrType = curr.PType
          override type TList = CurrType :: metaAcc.TList

          override type CodecTwiddlerT = metaAcc.CodecTwiddlerT ~ curr.PType

          override def codec: Codec[CodecTwiddlerT] = metaAcc.codec ~ currMeta.codec

          override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = new Twiddler[TList] {
            override type Out = CodecTwiddlerT

            override def to(h: TList): CodecTwiddlerT =
              (metaAcc.twiddler.to(h.tail), h.head)

            override def from(o: CodecTwiddlerT): TList =
              o._2 :: metaAcc.twiddler.from(o._1)
          }
        }

        if (currMeta.fields.isEmpty) (fields :+ curr.label, currMetaAux)
        else (fields ::: currMeta.fields, currMetaAux)
      }

      val generic = Generic[T]

      // FIXME this part is not type safe!!!
      val codec = metaAcc.codec.imap[T] { o =>
        // Looks bad
        val tList = metaAcc.twiddler.from(o)
        val genericTList = HList.unsafeReverse(tList).asInstanceOf[generic.Repr]
        generic.from(genericTList)
      } { i =>
        // Looks worse
        val genericTList = generic.to(i).asInstanceOf[HList]
        val tList = HList.unsafeReverse(genericTList).asInstanceOf[metaAcc.TList]
        metaAcc.twiddler.to(tList)
      }

      Meta(codec, params)
    }
  }
  def dispatch[T](ctx: SealedTrait[Meta, T]): Meta[T] =
    throw new UnsupportedOperationException("ADTs are not supported at the moment")
}
