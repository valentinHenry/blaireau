// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.utils

// TODO make this using macros
private[this] object AssignableMetaGenerator extends App {
  val nb = 25

  val functions = (1 to nb).map { last =>
    val u       = (1 to last).map(curr => s"U$curr")
    val uparam  = u.mkString(",")
    val ucodec  = u.mkString("~")
    val mparams = (1 to last).map(curr => s"u$curr: AssignableMeta[T, F, EF] => AssignmentAction[U$curr]").mkString(",")
    val uupdate = (1 to last).map(curr => s"u$curr(assignableMeta)").mkString("<+>")

    s"def update[$uparam]($mparams): UpdateCommand[$ucodec] = update($uupdate)"
  }

  println(functions.mkString("\n\n"))
}
