package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.Meta
import skunk.codec.UuidCodec

import java.util.UUID

trait UuidMetas extends UuidCodec {
  implicit val uuidMeta: Meta[UUID] = Meta(uuid, Nil)
}
