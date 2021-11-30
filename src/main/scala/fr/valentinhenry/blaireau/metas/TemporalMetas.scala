package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.Meta
import skunk.codec.TemporalCodecs

import java.time.{Duration, LocalDate, LocalDateTime, LocalTime, OffsetDateTime, OffsetTime}

trait TemporalMetas extends TemporalCodecs {
  implicit val localDateMeta: Meta[LocalDate]           = Meta(date, Nil)
  implicit val localTimeMeta: Meta[LocalTime]           = Meta(time, Nil)
  implicit val offsetTimeMeta: Meta[OffsetTime]         = Meta(timetz, Nil)
  implicit val localDateTimeMeta: Meta[LocalDateTime]   = Meta(timestamp, Nil)
  implicit val offsetDateTimeMeta: Meta[OffsetDateTime] = Meta(timestamptz, Nil)
  implicit val durationMeta: Meta[Duration]             = Meta(interval, Nil)
}
