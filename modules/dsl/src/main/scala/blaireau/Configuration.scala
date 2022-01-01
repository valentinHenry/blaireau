// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import skunk.Codec
import skunk.codec.all.text

case class Configuration(
  fieldFormatter: FieldFormatter = FieldFormatter.snake_case,
  stringCodec: Codec[String] = text,
  jsonTypeAsJsonb: Boolean = true // Used with blaireau-circe to chose between json and jsonb codec for the metas
)

object Configuration {
  implicit val default: Configuration = Configuration()
}

trait FieldFormatter {
  def formatField(str: String): String =
    if (str.isEmpty) throw new IllegalStateException("A field cannot be empty")
    else format(str)

  protected def format(str: String): String
}

object FieldFormatter {
  case object camelCase extends FieldFormatter {
    override protected def format(str: String): String = str
  }

  case object PascalCase extends FieldFormatter {
    override protected def format(str: String): String = s"${str.head.toUpper}${str.tail}"

  }

  case object ALLCAPS extends FieldFormatter {
    override protected def format(str: String): String = str.toUpperCase
  }

  case object lowercase extends FieldFormatter {
    override protected def format(str: String): String = str.toLowerCase
  }

  case object snake_case extends FieldFormatter {
    override protected def format(str: String): String = "[A-Z\\d]".r.replaceAllIn(
      str,
      m => "_" + m.group(0).toLowerCase()
    )
  }

  case object UPPER_SNAKE_CASE extends FieldFormatter {
    override protected def format(str: String): String = snake_case.formatField(str).toUpperCase
  }
}
