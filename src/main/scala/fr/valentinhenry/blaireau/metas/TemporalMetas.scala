package fr.valentinhenry.blaireau.metas

import fr.valentinhenry.blaireau.MetaType
import skunk.codec.TemporalCodecs

import java.time.{Duration, LocalDate, LocalDateTime, LocalTime, OffsetDateTime, OffsetTime}

trait TemporalMetas extends TemporalCodecs {
  implicit val localDateMeta: MetaType[LocalDate]           = MetaType(date)
  implicit val localTimeMeta: MetaType[LocalTime]           = MetaType(time)
  implicit val offsetTimeMeta: MetaType[OffsetTime]         = MetaType(timetz)
  implicit val localDateTimeMeta: MetaType[LocalDateTime]   = MetaType(timestamp)
  implicit val offsetDateTimeMeta: MetaType[OffsetDateTime] = MetaType(timestamptz)
  implicit val durationMeta: MetaType[Duration]             = MetaType(interval)
}
