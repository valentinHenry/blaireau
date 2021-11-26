package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.MetaType
import skunk.codec.BooleanCodec

trait BooleanMetas extends BooleanCodec {
  implicit val booleanMeta: MetaType[Boolean] = MetaType(bool)
}
