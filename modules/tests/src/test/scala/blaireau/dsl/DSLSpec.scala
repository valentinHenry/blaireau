package blaireau.dsl

import blaireau.metas.{ExportedMeta, MetaField}

object DSLSpec extends App {
  import blaireau.generic.codec.instances.all._
  import shapeless.record._

  final case class IDK(yes: String, no: Int, maybe: Float)

  val exportedIDK = ExportedMeta[IDK]

  val idkFields = exportedIDK.meta.metaFields

  val pray: MetaField[String] = idkFields.get('yes)
  println(pray)
  println(idkFields)
  println(exportedIDK.meta.fieldNames)

//  val exportedHmm = the[ExportedMeta[Hmm]]
//  println(exportedHmm.meta.fieldNames)

}
