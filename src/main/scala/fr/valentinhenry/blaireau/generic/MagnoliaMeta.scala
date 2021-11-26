package fr.valentinhenry.blaireau.generic

import fr.valentinhenry.blaireau.{Meta, MetaCodec, MetaField, MetaType}
import fr.valentinhenry.blaireau.metas.MetaCodec
import magnolia._

import scala.language.experimental.macros

private[generic] object MagnoliaMeta {
  def combine[T](ctx: CaseClass[Meta, T]): MetaCodec[T] = {
    val fields = ctx.parameters.toList.flatMap { param =>
      val paramName = param.label

      param.typeclass match {
        case MetaType(fieldType) => MetaField(fieldType, paramName) :: Nil
        case MetaCodec(fields)   => fields
      }
    }

    new MetaCodec[T](fields)
  }

  def dispatch[T](sealedTrait: SealedTrait[Meta, T]): Meta[T] = ???
}
