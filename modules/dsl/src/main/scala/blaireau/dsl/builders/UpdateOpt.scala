// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.assignment.AssignmentAction
import blaireau.metas.Meta
import shapeless.HList
import skunk.~

trait UpdateOpt[T, F <: HList, MF <: HList, EF <: HList] {
  type UpdateCommand[U] = UpdateCommandBuilder[T, F, MF, EF, U, skunk.Void]

  def meta: Meta.Aux[T, F, MF, EF]

  def update[U](u: Meta.Aux[T, F, MF, EF] => AssignmentAction[U]): UpdateCommand[U] =
    update(u(meta))

  def update[U1, U2](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2]
  ): UpdateCommand[U1 ~ U2] = update(u1(meta) <+> u2(meta))

  def update[U1, U2, U3](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3]
  ): UpdateCommand[U1 ~ U2 ~ U3] = update(u1(meta) <+> u2(meta) <+> u3(meta))

  def update[U1, U2, U3, U4](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta)
  )

  def update[U1, U2, U3, U4, U5](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta)
  )

  def update[U1, U2, U3, U4, U5, U6](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7],
    u8: Meta.Aux[T, F, MF, EF] => AssignmentAction[U8]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7 ~
      U8
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta) <+>
      u8(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7],
    u8: Meta.Aux[T, F, MF, EF] => AssignmentAction[U8],
    u9: Meta.Aux[T, F, MF, EF] => AssignmentAction[U9]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7 ~
      U8 ~
      U9
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta) <+>
      u8(meta) <+>
      u9(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7],
    u8: Meta.Aux[T, F, MF, EF] => AssignmentAction[U8],
    u9: Meta.Aux[T, F, MF, EF] => AssignmentAction[U9],
    u10: Meta.Aux[T, F, MF, EF] => AssignmentAction[U10]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7 ~
      U8 ~
      U9 ~
      U10
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta) <+>
      u8(meta) <+>
      u9(meta) <+>
      u10(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7],
    u8: Meta.Aux[T, F, MF, EF] => AssignmentAction[U8],
    u9: Meta.Aux[T, F, MF, EF] => AssignmentAction[U9],
    u10: Meta.Aux[T, F, MF, EF] => AssignmentAction[U10],
    u11: Meta.Aux[T, F, MF, EF] => AssignmentAction[U11]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7 ~
      U8 ~
      U9 ~
      U10 ~
      U11
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta) <+>
      u8(meta) <+>
      u9(meta) <+>
      u10(meta) <+>
      u11(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7],
    u8: Meta.Aux[T, F, MF, EF] => AssignmentAction[U8],
    u9: Meta.Aux[T, F, MF, EF] => AssignmentAction[U9],
    u10: Meta.Aux[T, F, MF, EF] => AssignmentAction[U10],
    u11: Meta.Aux[T, F, MF, EF] => AssignmentAction[U11],
    u12: Meta.Aux[T, F, MF, EF] => AssignmentAction[U12]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7 ~
      U8 ~
      U9 ~
      U10 ~
      U11 ~
      U12
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta) <+>
      u8(meta) <+>
      u9(meta) <+>
      u10(meta) <+>
      u11(meta) <+>
      u12(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7],
    u8: Meta.Aux[T, F, MF, EF] => AssignmentAction[U8],
    u9: Meta.Aux[T, F, MF, EF] => AssignmentAction[U9],
    u10: Meta.Aux[T, F, MF, EF] => AssignmentAction[U10],
    u11: Meta.Aux[T, F, MF, EF] => AssignmentAction[U11],
    u12: Meta.Aux[T, F, MF, EF] => AssignmentAction[U12],
    u13: Meta.Aux[T, F, MF, EF] => AssignmentAction[U13]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7 ~
      U8 ~
      U9 ~
      U10 ~
      U11 ~
      U12 ~
      U13
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta) <+>
      u8(meta) <+>
      u9(meta) <+>
      u10(meta) <+>
      u11(meta) <+>
      u12(meta) <+>
      u13(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7],
    u8: Meta.Aux[T, F, MF, EF] => AssignmentAction[U8],
    u9: Meta.Aux[T, F, MF, EF] => AssignmentAction[U9],
    u10: Meta.Aux[T, F, MF, EF] => AssignmentAction[U10],
    u11: Meta.Aux[T, F, MF, EF] => AssignmentAction[U11],
    u12: Meta.Aux[T, F, MF, EF] => AssignmentAction[U12],
    u13: Meta.Aux[T, F, MF, EF] => AssignmentAction[U13],
    u14: Meta.Aux[T, F, MF, EF] => AssignmentAction[U14]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7 ~
      U8 ~
      U9 ~
      U10 ~
      U11 ~
      U12 ~
      U13 ~
      U14
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta) <+>
      u8(meta) <+>
      u9(meta) <+>
      u10(meta) <+>
      u11(meta) <+>
      u12(meta) <+>
      u13(meta) <+>
      u14(meta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15](
    u1: Meta.Aux[T, F, MF, EF] => AssignmentAction[U1],
    u2: Meta.Aux[T, F, MF, EF] => AssignmentAction[U2],
    u3: Meta.Aux[T, F, MF, EF] => AssignmentAction[U3],
    u4: Meta.Aux[T, F, MF, EF] => AssignmentAction[U4],
    u5: Meta.Aux[T, F, MF, EF] => AssignmentAction[U5],
    u6: Meta.Aux[T, F, MF, EF] => AssignmentAction[U6],
    u7: Meta.Aux[T, F, MF, EF] => AssignmentAction[U7],
    u8: Meta.Aux[T, F, MF, EF] => AssignmentAction[U8],
    u9: Meta.Aux[T, F, MF, EF] => AssignmentAction[U9],
    u10: Meta.Aux[T, F, MF, EF] => AssignmentAction[U10],
    u11: Meta.Aux[T, F, MF, EF] => AssignmentAction[U11],
    u12: Meta.Aux[T, F, MF, EF] => AssignmentAction[U12],
    u13: Meta.Aux[T, F, MF, EF] => AssignmentAction[U13],
    u14: Meta.Aux[T, F, MF, EF] => AssignmentAction[U14],
    u15: Meta.Aux[T, F, MF, EF] => AssignmentAction[U15]
  ): UpdateCommand[
    U1 ~
      U2 ~
      U3 ~
      U4 ~
      U5 ~
      U6 ~
      U7 ~
      U8 ~
      U9 ~
      U10 ~
      U11 ~
      U12 ~
      U13 ~
      U14 ~
      U15
  ] = update(
    u1(meta) <+>
      u2(meta) <+>
      u3(meta) <+>
      u4(meta) <+>
      u5(meta) <+>
      u6(meta) <+>
      u7(meta) <+>
      u8(meta) <+>
      u9(meta) <+>
      u10(meta) <+>
      u11(meta) <+>
      u12(meta) <+>
      u13(meta) <+>
      u14(meta) <+>
      u15(meta)
  )

  protected[this] def update[U](updates: AssignmentAction[U]): UpdateCommand[U]
}
