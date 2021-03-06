// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances

import blaireau.metas.{Meta, MetaS}
import skunk.codec.temporal._

import java.time._

trait TemporalMetaInstances {
  implicit val localDateMeta: MetaS[LocalDate]           = Meta.of(date)
  implicit val localTimeMeta: MetaS[LocalTime]           = Meta.of(time)
  implicit val offsetTimeMeta: MetaS[OffsetTime]         = Meta.of(timetz)
  implicit val localDateTimeMeta: MetaS[LocalDateTime]   = Meta.of(timestamp)
  implicit val offsetDateTimeMeta: MetaS[OffsetDateTime] = Meta.of(timestamptz)
  implicit val durationMeta: MetaS[Duration]             = Meta.of(interval)
}
