package fr.valentinhenry.blaireau

import skunk.Codec

sealed trait Meta[+T]

case class MetaType[T](
  codec: Codec[T]
) extends Meta[T]

case class MetaField[T](
  fieldType: Codec[T],
  fieldName: String
)

case class MetaCodec[A](
  fields: List[MetaField[_]]
) extends Meta[A]
