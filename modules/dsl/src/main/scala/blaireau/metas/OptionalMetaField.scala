package blaireau.metas

trait OptionalMetaField[H] extends MetaField[Option[H]] {
  private[blaireau] def internal: MetaField[H]
}
