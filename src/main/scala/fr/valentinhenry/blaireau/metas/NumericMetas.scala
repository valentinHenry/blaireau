package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.MetaType
import skunk.codec.NumericCodecs

trait NumericMetas extends NumericCodecs {
  implicit val shortMeta: MetaType[Short]           = MetaType(int2)
  implicit val intMeta: MetaType[Int]               = MetaType(int4)
  implicit val longMeta: MetaType[Long]             = MetaType(int8)
  implicit val bigDecimalMeta: MetaType[BigDecimal] = MetaType(numeric)
  implicit val floatMeta: MetaType[Float]           = MetaType(float4)
  implicit val doubleMeta: MetaType[Double]         = MetaType(float8)
}
