// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas

import blaireau.dsl.actions.IMapper
import shapeless.HList
import shapeless.ops.hlist.{LeftReducer, Mapper}
import skunk.util.Twiddler

object MetaUtils {
  def applyExtract[MFun, LRFun, A, EF <: HList, MO <: HList, FO, CO, OutT[_]](
    elt: A,
    extract: A => EF
  )(implicit
    mapper: Mapper.Aux[MFun, EF, MO],
    reducer: LeftReducer.Aux[MO, LRFun, FO],
    ev: FO =:= OutT[CO],
    tw: Twiddler.Aux[A, CO],
    im: IMapper[OutT]
  ): OutT[A] =
    im.imap(
      ev(
        reducer(
          mapper(
            extract(elt)
          )
        )
      )
    )(tw.from)(tw.to)
}
