package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.Meta
import skunk.codec.BooleanCodec

trait BooleanMetas extends BooleanCodec {
  implicit val booleanMeta: Meta[Boolean] = Meta(bool, Nil)
}
