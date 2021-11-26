package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.MetaType
import skunk.codec.TextCodecs

trait TextMetas extends TextCodecs {
  //TODO find a solution for bpchar and varchar...
  implicit val stringMeta: MetaType[String] = MetaType(text)
}
