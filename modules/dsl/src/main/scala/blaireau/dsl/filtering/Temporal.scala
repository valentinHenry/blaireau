// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.filtering

import java.time.{Duration, LocalDate, LocalDateTime, LocalTime, OffsetDateTime, OffsetTime}

trait Temporal[T]

object Temporal {
  final def instance[T]: Temporal[T]                    = new Temporal[T] {}
  implicit def localDate: Temporal[LocalDate]           = instance[LocalDate]
  implicit def localTime: Temporal[LocalTime]           = instance[LocalTime]
  implicit def offerTime: Temporal[OffsetTime]          = instance[OffsetTime]
  implicit def localDateTime: Temporal[LocalDateTime]   = instance[LocalDateTime]
  implicit def offsetDateTime: Temporal[OffsetDateTime] = instance[OffsetDateTime]
  implicit def duration: Temporal[Duration]             = instance[Duration]
}
