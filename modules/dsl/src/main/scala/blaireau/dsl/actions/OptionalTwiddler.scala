// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.actions

import cats.syntax.all._
import shapeless.ops.hlist.{Init, Last, Prepend}
import shapeless.{::, Generic, HList, HNil, Lazy}
import skunk.util.Twiddler

trait OptionalTwiddler[A] {
  type Out

  def to(a: Option[A]): Out

  def from(o: Out): Option[A]
}

object OptionalTwiddler {
  type Aux[A, O] = OptionalTwiddler[A] { type Out = O }

  implicit def generic[A, BO, O](implicit
    generic: Generic.Aux[A, BO],
    tw: Lazy[OptionalTwiddler.Aux[BO, O]]
  ): OptionalTwiddler.Aux[A, O] =
    new OptionalTwiddler[A] {
      type Out = O

      override def to(a: Option[A]): Out = tw.value.to(a.map(generic.to))

      override def from(o: Out): Option[A] = tw.value.from(o).map(generic.from)
    }

  implicit def base[A]: OptionalTwiddler.Aux[A :: HNil, Option[A]] =
    new OptionalTwiddler[A :: HNil] {
      type Out = Option[A]

      override def from(o: Option[A]): Option[A :: HNil] = o.map(_ :: HNil)

      override def to(a: Option[A :: HNil]): Option[A] = a.map(_.head)
    }

  implicit def inductive[A <: HList, B <: HList, BC, L](implicit
    in: Init.Aux[A, B],
    la: Last.Aux[A, L],
    tw: OptionalTwiddler.Aux[B, BC],
    pp: Prepend.Aux[B, L :: HNil, A]
  ): OptionalTwiddler.Aux[A, (BC, Option[L])] =
    new OptionalTwiddler[A] {
      type Out = (BC, Option[L])

      override def to(a: Option[A]): Out = {
        val before: Option[B] = a.map(in(_))
        val last: Option[L]   = a.map(la(_))

        (tw.to(before), last)
      }

      override def from(o: (BC, Option[L])): Option[A] =
        (tw.from(o._1), o._2).mapN((bc, l) => bc :+ l)

    }
}

trait TwiddlerInstances {
  implicit def base[A, B](implicit optionalTwiddler: OptionalTwiddler.Aux[A, B]): Twiddler.Aux[Option[A], B] =
    new Twiddler[Option[A]] {
      override final type Out = B

      override def to(h: Option[A]): Out = optionalTwiddler.to(h)

      override def from(o: Out): Option[A] = optionalTwiddler.from(o)
    }
}
