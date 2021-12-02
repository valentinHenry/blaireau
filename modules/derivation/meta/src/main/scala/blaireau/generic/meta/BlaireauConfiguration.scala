// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.generic.meta

case class BlaireauConfiguration(
  sqlFieldFormat: FieldFormat = FieldFormat.SnakeCase
)(
  val formatter: Formatter = Formatter.of(sqlFieldFormat)
)

object BlaireauConfiguration {
  def default: BlaireauConfiguration = BlaireauConfiguration()()
}
