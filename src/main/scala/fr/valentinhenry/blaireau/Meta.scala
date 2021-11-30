package fr.valentinhenry.blaireau

import cats.data.State
import skunk.data.Type
import skunk.{Codec, Decoder}

trait Meta[T] extends Codec[T] {
  def fields: List[String]

  def asCodec: Codec[T] = this

  override def imap[B](f: T => B)(g: B => T): Meta[B] =
    Meta(super.imap(f)(g), fields)
}
object Meta {
  def apply[A](codec: Codec[A], f: List[String]): Meta[A] =
    new Meta[A] {
      override def fields: List[String] = f
      override def sql: State[Int, String] = codec.sql
      override def encode(a: A): List[Option[String]] = codec.encode(a)
      override def types: List[Type] = codec.types
      override def decode(offset: Int, ss: List[Option[String]]): Either[Decoder.Error, A] = codec.decode(offset, ss)
    }
}