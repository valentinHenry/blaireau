// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.codec.instances

import skunk.Codec
import skunk.codec.TemporalCodecs

import java.time._

trait TemporalCodecInstances extends TemporalCodecs {
  implicit val localDateCodec: Codec[LocalDate]           = date
  implicit val localTimeCodec: Codec[LocalTime]           = time
  implicit val offsetTimeCodec: Codec[OffsetTime]         = timetz
  implicit val localDateTimeCodec: Codec[LocalDateTime]   = timestamp
  implicit val offsetDateTimeCodec: Codec[OffsetDateTime] = timestamptz
  implicit val durationCodec: Codec[Duration]             = interval
}
