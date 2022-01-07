// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.dsl.actions.IMapper
import shapeless.ops.hlist.{LeftReducer, Mapper}
import shapeless.{HList, HNil}
import skunk.util.Twiddler

trait ExtractApplier[MFun, LRFun, T, EF, OutT[_]] {
  self =>
  def imapper: IMapper[OutT]

  def apply(extracted: EF): OutT[T]

  def imap[B](f: T => B)(g: B => T): ExtractApplier[MFun, LRFun, B, EF, OutT] =
    new ExtractApplier[MFun, LRFun, B, EF, OutT] {
      override def imapper: IMapper[OutT] = self.imapper

      override def apply(extracted: EF): OutT[B] = imapper.imap(self(extracted))(f)(g)
    }
}

object ExtractApplier {
  implicit def instance[MFun, LRFun, T, EF <: HList, MO <: HList, FO, CO, OutT[_]](implicit
    mapper: Mapper.Aux[MFun, EF, MO],
    reducer: LeftReducer.Aux[MO, LRFun, FO],
    ev: FO =:= OutT[CO],
    tw: Twiddler.Aux[T, CO],
    im: IMapper[OutT]
  ): ExtractApplier[MFun, LRFun, T, EF, OutT] =
    new ExtractApplier[MFun, LRFun, T, EF, OutT] {
      override def imapper: IMapper[OutT] = im

      override def apply(extracted: EF): OutT[T] =
        imapper.imap(
          ev(
            reducer(
              mapper(
                extracted
              )
            )
          )
        )(tw.from)(tw.to)
    }

  implicit def fieldExtractApplier[MFun, LRFun, T, OutT[_]](implicit
    im: IMapper[OutT]
  ): ExtractApplier[MFun, LRFun, T, HNil, OutT] =
    new ExtractApplier[MFun, LRFun, T, HNil, OutT] {
      override def imapper: IMapper[OutT] = im

      override def apply(extracted: HNil): OutT[T] = throw new IllegalStateException(
        "it is impossible to extract on fields"
      )
    }
}
