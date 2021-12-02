// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec

import magnolia.{CaseClass, Param, SealedTrait}
import shapeless.{Generic, HList}
import skunk._
import skunk.util.Twiddler

private[generic] object MagnoliaCodec {
  trait CodecAcc {
    type CurrType
    type CodecTwiddlerT
    type TList <: HList

    def codec: Codec[CodecTwiddlerT]
    def twiddler: Twiddler.Aux[TList, CodecTwiddlerT]
  }

  object CodecAcc {
    import shapeless._
    def first[T](firstParam: Param[Codec, T]): CodecAcc =
      new CodecAcc {
        override type CurrType       = firstParam.PType
        override type TList          = CurrType :: HNil
        override type CodecTwiddlerT = firstParam.PType

        override def codec: Codec[CodecTwiddlerT]                  = firstParam.typeclass
        override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = Twiddler.base
      }

    def product[T](curr: Param[Codec, T], codecAcc: CodecAcc): CodecAcc =
      new CodecAcc {
        override type CurrType = curr.PType
        override type TList    = CurrType :: codecAcc.TList

        override type CodecTwiddlerT = curr.PType ~ codecAcc.CodecTwiddlerT

        override def codec: Codec[CodecTwiddlerT] = curr.typeclass ~ codecAcc.codec

        override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = new Twiddler[TList] {
          override type Out = CodecTwiddlerT

          override def to(h: TList): CodecTwiddlerT =
            (h.head, codecAcc.twiddler.to(h.tail))

          override def from(o: CodecTwiddlerT): TList =
            o._1 :: codecAcc.twiddler.from(o._2)
        }
      }
  }

  def combine[T: Generic](ctx: CaseClass[Codec, T]): Codec[T] =
    ctx.parameters.toList match {
      case Nil =>
        Void.codec.imap(_ => ctx.rawConstruct(Seq.empty[Any]))(_ => Void)

      case param :: Nil if ctx.isValueClass =>
        param.typeclass.imap[T](p => ctx.rawConstruct(Seq(p)))(cc => param.dereference(cc))

      case params =>
        val lastCodecAcc: CodecAcc = CodecAcc.first(params.last)

        val codecAcc = params.take(params.size - 1).foldRight(lastCodecAcc)(CodecAcc.product)

        val generic = Generic[T]

        // FIXME this part is not type safe!!!
        codecAcc.codec.imap[T] { o =>
          // Looks bad
          val tList        = codecAcc.twiddler.from(o)
          val genericTList = tList.asInstanceOf[generic.Repr]
          generic.from(genericTList)
        } { i =>
          // Looks worse
          val genericTList = generic.to(i).asInstanceOf[HList]
          val tList        = genericTList.asInstanceOf[codecAcc.TList]
          codecAcc.twiddler.to(tList)
        }
    }

  def dispatch[T](ctx: SealedTrait[Codec, T]): Codec[T] =
    throw new UnsupportedOperationException("ADTs are not supported at the moment")
}
