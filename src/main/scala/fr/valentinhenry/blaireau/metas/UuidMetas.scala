package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.MetaType
import skunk.codec.UuidCodec

import java.util.UUID

trait UuidMetas extends UuidCodec {
  implicit val uuidMeta: MetaType[UUID] = MetaType(uuid)
}
