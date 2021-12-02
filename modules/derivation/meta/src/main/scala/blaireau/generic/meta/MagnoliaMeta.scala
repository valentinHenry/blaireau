// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.meta

import blaireau.{Meta, MetaField}
import magnolia.{CaseClass, Param, SealedTrait}
import shapeless.{Generic, HList}
import skunk.util.Twiddler
import skunk.{Codec, Void, ~}

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
    def last[T](firstParam: Param[Meta, T]): MetaAcc =
      new MetaAcc {
        override type CurrType = firstParam.PType

        override type TList          = CurrType :: HNil
        override type CodecTwiddlerT = firstParam.PType

        override def codec: Codec[CodecTwiddlerT] = firstParam.typeclass.codec

        override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = Twiddler.base
      }

    def product[T](metaAcc: MetaAcc, curr: Param[Meta, T]): MetaAcc =
      new MetaAcc {
        override type CurrType = curr.PType
        override type TList    = CurrType :: metaAcc.TList

        override type CodecTwiddlerT = curr.PType ~ metaAcc.CodecTwiddlerT

        override def codec: Codec[CodecTwiddlerT] = curr.typeclass.codec ~ metaAcc.codec

        override def twiddler: Twiddler.Aux[TList, CodecTwiddlerT] = new Twiddler[TList] {
          override type Out = CodecTwiddlerT

          override def to(h: TList): CodecTwiddlerT =
            (h.head, metaAcc.twiddler.to(h.tail))

          override def from(o: CodecTwiddlerT): TList =
            o._1 :: metaAcc.twiddler.from(o._2)
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

      case ctxParams =>
        val formatter = config.formatter

        def makeField(p: Param[Meta, T]): MetaField = new MetaField {
          override private[blaireau] type FieldType = p.PType

          override private[blaireau] def name: String    = p.label
          override private[blaireau] def sqlName: String = formatter(p.label)

          override private[blaireau] def codec: Codec[FieldType] = p.typeclass.codec
        }

        val lastParams = ctxParams.last

        val lastField: List[MetaField] =
          if (lastParams.typeclass.fields.isEmpty) makeField(lastParams) :: Nil else lastParams.typeclass.fields

        val lastMetaAcc: MetaAcc = MetaAcc.last(lastParams)

        val (fields, metaAcc) =
          ctxParams.take(ctxParams.size - 1).foldRight((lastField, lastMetaAcc)) { case (curr, (fields, metaAcc)) =>
            val currMeta: Meta[curr.PType] = curr.typeclass
            val currMetaAcc: MetaAcc       = MetaAcc.product(metaAcc, curr)

            if (currMeta.fields.isEmpty) (makeField(curr) :: fields, currMetaAcc)
            else (currMeta.fields ::: fields, currMetaAcc)
          }

        val generic = Generic[T]

        // FIXME this part is not type safe!!!
        val codec = metaAcc.codec.imap[T] { o =>
          // Looks bad
          val tList        = metaAcc.twiddler.from(o)
          val genericTList = tList.asInstanceOf[generic.Repr]
          generic.from(genericTList)
        } { i =>
          // Looks worse
          val genericTList = generic.to(i).asInstanceOf[HList]
          val tList        = genericTList.asInstanceOf[metaAcc.TList]
          metaAcc.twiddler.to(tList)
        }

        Meta(codec, fields)
    }

  def dispatch[T](ctx: SealedTrait[Meta, T]): Meta[T] =
    throw new UnsupportedOperationException("ADTs are not supported at the moment")
}
