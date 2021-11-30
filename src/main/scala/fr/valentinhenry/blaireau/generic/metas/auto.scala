package fr.valentinhenry.blaireau.generic.metas

import fr.valentinhenry.blaireau.Meta
import fr.valentinhenry.blaireau.generic.MagnoliaMeta
import fr.valentinhenry.blaireau.metas.AllMetas
import magnolia.{CaseClass, Magnolia, SealedTrait}
import shapeless.Generic

import scala.language.experimental.macros

object auto extends AllMetas {
  type Typeclass[T] = Meta[T]

  def combine[T: Generic](ctx: CaseClass[Typeclass, T]): Typeclass[T] = {
    def assertFieldsIntegrity(meta: Meta[T]): Unit = {
      val groupedFields = meta.fields.groupBy(identity)
      assert(
        groupedFields.keys.size == meta.fields.size,
        {
          val duplicatedFields = groupedFields.filter(_._2.size >= 2).keys
          s"""\nFailed to create Meta for class ${ctx.typeName.short}. It has duplicated fields:
             |${duplicatedFields.mkString(" - ", "\n - ", "")}""".stripMargin
        }
      )
    }

    val meta = MagnoliaMeta.combine[T](ctx)
    assertFieldsIntegrity(meta)
    meta
  }

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    MagnoliaMeta.dispatch[T](sealedTrait)

  implicit def deriveAuto[T]: Typeclass[T] = macro Magnolia.gen[T]
}
