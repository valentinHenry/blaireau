package fr.valentinhenry.blaireau.generic

import fr.valentinhenry.blaireau.Meta
import magnolia.{CaseClass, SealedTrait}
import skunk._
import skunk.util.Twiddler
import shapeless._
import shapeless.HList

import scala.language.experimental.macros

//TODO find a way to make it type safe...
private[generic] object MagnoliaMeta {
  trait MetaAux{
    type CurrType

    type CodecTwiddlerT
    type TList <: HList

    def codec: Codec[CodecTwiddlerT]

    def twiddler: Twiddler.Aux[TList, CodecTwiddlerT]
  }

  def combine[T: Generic](ctx: CaseClass[Meta, T]): Meta[T] = {
    if (ctx.isValueClass){
      val param = ctx.parameters.head
      val codec = param.typeclass.imap[T](p => ctx.rawConstruct(Seq(p)))(cc => param.dereference(cc))
      Meta(codec, Nil)
    } else {
      val parameters = ctx.parameters
      val firstParam = parameters.head
      val firstFields = if (firstParam.typeclass.fields.isEmpty) firstParam.label :: Nil else firstParam.typeclass.fields
      val firstMetaAux: MetaAux = new MetaAux {
        override type CurrType = firstParam.PType

        override type TList = CurrType :: HNil
        override type CodecTwiddlerT = firstParam.PType

        override def codec: Codec[CodecTwiddlerT] = firstParam.typeclass

        override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = Twiddler.base
      }

      val (params, metaAux) = parameters.tail.foldLeft((firstFields, firstMetaAux)){ case ((fields, metaAux), curr) =>
        val tc = curr.typeclass
        val currMetaAux: MetaAux = new MetaAux {
          override type CurrType = curr.PType
          override type TList = CurrType :: metaAux.TList

          override type CodecTwiddlerT = metaAux.CodecTwiddlerT ~ curr.PType

          override def codec: Codec[CodecTwiddlerT] = metaAux.codec ~ curr.typeclass

          override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = new Twiddler[TList] {
            override type Out = CodecTwiddlerT

            override def to(h: TList): CodecTwiddlerT =
              (metaAux.twiddler.to(h.tail), h.head)

            override def from(o: CodecTwiddlerT): TList =
              o._2 :: metaAux.twiddler.from(o._1)
          }
        }

        if (tc.fields.isEmpty) (fields :+ curr.label, currMetaAux)
        else (fields ::: tc.fields, currMetaAux)
      }

      val generic = Generic[T]

      val codec = metaAux.codec.imap[T] { o =>
        // Looks bad
        val tList = metaAux.twiddler.from(o)
        val genericTList = HList.unsafeReverse(tList).asInstanceOf[generic.Repr]
        generic.from(genericTList)
      } { i =>
        // Looks worse
        val genericTList = generic.to(i).asInstanceOf[HList]
        val tList = HList.unsafeReverse(genericTList).asInstanceOf[metaAux.TList]
        metaAux.twiddler.to(tList)
      }

      Meta(codec, params)
    }
  }
  def dispatch[T](ctx: SealedTrait[Meta, T]): Meta[T] =
    throw new UnsupportedOperationException("ADTs are not supported at the moment")
}
