// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.dsl.builders

import blaireau.dsl.assignment.{AssignableMeta, AssignmentAction}
import blaireau.metas.Meta
import shapeless.HList
import skunk.~

trait UpdateOpt[T, F <: HList, MF <: HList, EF <: HList] {
  type UpdateCommand[U] = UpdateCommandBuilder[T, F, MF, EF, U, skunk.Void]

  def meta: Meta.Aux[T, F, MF, EF]

  def update[U1](u1: AssignableMeta[T, F, EF] => AssignmentAction[U1]): UpdateCommand[U1] = update(
    u1(assignableMeta)
  )

  // DO NOT MODIFY, these functions are generated using the AssignableMetaGenerator

  def update[U1, U2](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2]
  ): UpdateCommand[U1 ~ U2] = update(u1(assignableMeta) <+> u2(assignableMeta))

  def update[U1, U2, U3](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3]
  ): UpdateCommand[U1 ~ U2 ~ U3] = update(u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta))

  def update[U1, U2, U3, U4](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    )
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta)
  )

  private[this] def assignableMeta: AssignableMeta[T, F, EF] = AssignableMeta.makeSelectable(meta)

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    )
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta) <+> u17(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17, U18](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17],
    u18: AssignableMeta[T, F, EF] => AssignmentAction[U18]
  ): UpdateCommand[U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17 ~ U18] =
    update(
      u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
        assignableMeta
      ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
        assignableMeta
      ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
        assignableMeta
      ) <+> u16(assignableMeta) <+> u17(assignableMeta) <+> u18(assignableMeta)
    )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17, U18, U19](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17],
    u18: AssignableMeta[T, F, EF] => AssignmentAction[U18],
    u19: AssignableMeta[T, F, EF] => AssignmentAction[U19]
  ): UpdateCommand[
    U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17 ~ U18 ~ U19
  ] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta) <+> u17(assignableMeta) <+> u18(assignableMeta) <+> u19(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17, U18, U19, U20](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17],
    u18: AssignableMeta[T, F, EF] => AssignmentAction[U18],
    u19: AssignableMeta[T, F, EF] => AssignmentAction[U19],
    u20: AssignableMeta[T, F, EF] => AssignmentAction[U20]
  ): UpdateCommand[
    U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17 ~ U18 ~ U19 ~ U20
  ] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta) <+> u17(assignableMeta) <+> u18(assignableMeta) <+> u19(assignableMeta) <+> u20(
      assignableMeta
    )
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17, U18, U19, U20, U21](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17],
    u18: AssignableMeta[T, F, EF] => AssignmentAction[U18],
    u19: AssignableMeta[T, F, EF] => AssignmentAction[U19],
    u20: AssignableMeta[T, F, EF] => AssignmentAction[U20],
    u21: AssignableMeta[T, F, EF] => AssignmentAction[U21]
  ): UpdateCommand[
    U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17 ~ U18 ~ U19 ~ U20 ~ U21
  ] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta) <+> u17(assignableMeta) <+> u18(assignableMeta) <+> u19(assignableMeta) <+> u20(
      assignableMeta
    ) <+> u21(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17, U18, U19, U20, U21, U22](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17],
    u18: AssignableMeta[T, F, EF] => AssignmentAction[U18],
    u19: AssignableMeta[T, F, EF] => AssignmentAction[U19],
    u20: AssignableMeta[T, F, EF] => AssignmentAction[U20],
    u21: AssignableMeta[T, F, EF] => AssignmentAction[U21],
    u22: AssignableMeta[T, F, EF] => AssignmentAction[U22]
  ): UpdateCommand[
    U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17 ~ U18 ~ U19 ~ U20 ~ U21 ~ U22
  ] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta) <+> u17(assignableMeta) <+> u18(assignableMeta) <+> u19(assignableMeta) <+> u20(
      assignableMeta
    ) <+> u21(assignableMeta) <+> u22(assignableMeta)
  )

  def update[U1, U2, U3, U4, U5, U6, U7, U8, U9, U10, U11, U12, U13, U14, U15, U16, U17, U18, U19, U20, U21, U22, U23](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17],
    u18: AssignableMeta[T, F, EF] => AssignmentAction[U18],
    u19: AssignableMeta[T, F, EF] => AssignmentAction[U19],
    u20: AssignableMeta[T, F, EF] => AssignmentAction[U20],
    u21: AssignableMeta[T, F, EF] => AssignmentAction[U21],
    u22: AssignableMeta[T, F, EF] => AssignmentAction[U22],
    u23: AssignableMeta[T, F, EF] => AssignmentAction[U23]
  ): UpdateCommand[
    U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17 ~ U18 ~ U19 ~ U20 ~ U21 ~ U22 ~ U23
  ] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta) <+> u17(assignableMeta) <+> u18(assignableMeta) <+> u19(assignableMeta) <+> u20(
      assignableMeta
    ) <+> u21(assignableMeta) <+> u22(assignableMeta) <+> u23(assignableMeta)
  )

  def update[
    U1,
    U2,
    U3,
    U4,
    U5,
    U6,
    U7,
    U8,
    U9,
    U10,
    U11,
    U12,
    U13,
    U14,
    U15,
    U16,
    U17,
    U18,
    U19,
    U20,
    U21,
    U22,
    U23,
    U24
  ](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17],
    u18: AssignableMeta[T, F, EF] => AssignmentAction[U18],
    u19: AssignableMeta[T, F, EF] => AssignmentAction[U19],
    u20: AssignableMeta[T, F, EF] => AssignmentAction[U20],
    u21: AssignableMeta[T, F, EF] => AssignmentAction[U21],
    u22: AssignableMeta[T, F, EF] => AssignmentAction[U22],
    u23: AssignableMeta[T, F, EF] => AssignmentAction[U23],
    u24: AssignableMeta[T, F, EF] => AssignmentAction[U24]
  ): UpdateCommand[
    U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17 ~ U18 ~ U19 ~ U20 ~ U21 ~ U22 ~ U23 ~ U24
  ] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta) <+> u17(assignableMeta) <+> u18(assignableMeta) <+> u19(assignableMeta) <+> u20(
      assignableMeta
    ) <+> u21(assignableMeta) <+> u22(assignableMeta) <+> u23(assignableMeta) <+> u24(assignableMeta)
  )

  def update[
    U1,
    U2,
    U3,
    U4,
    U5,
    U6,
    U7,
    U8,
    U9,
    U10,
    U11,
    U12,
    U13,
    U14,
    U15,
    U16,
    U17,
    U18,
    U19,
    U20,
    U21,
    U22,
    U23,
    U24,
    U25
  ](
    u1: AssignableMeta[T, F, EF] => AssignmentAction[U1],
    u2: AssignableMeta[T, F, EF] => AssignmentAction[U2],
    u3: AssignableMeta[T, F, EF] => AssignmentAction[U3],
    u4: AssignableMeta[T, F, EF] => AssignmentAction[U4],
    u5: AssignableMeta[T, F, EF] => AssignmentAction[U5],
    u6: AssignableMeta[T, F, EF] => AssignmentAction[U6],
    u7: AssignableMeta[T, F, EF] => AssignmentAction[U7],
    u8: AssignableMeta[T, F, EF] => AssignmentAction[U8],
    u9: AssignableMeta[T, F, EF] => AssignmentAction[U9],
    u10: AssignableMeta[T, F, EF] => AssignmentAction[U10],
    u11: AssignableMeta[T, F, EF] => AssignmentAction[U11],
    u12: AssignableMeta[T, F, EF] => AssignmentAction[U12],
    u13: AssignableMeta[T, F, EF] => AssignmentAction[U13],
    u14: AssignableMeta[T, F, EF] => AssignmentAction[U14],
    u15: AssignableMeta[T, F, EF] => AssignmentAction[U15],
    u16: AssignableMeta[T, F, EF] => AssignmentAction[U16],
    u17: AssignableMeta[T, F, EF] => AssignmentAction[U17],
    u18: AssignableMeta[T, F, EF] => AssignmentAction[U18],
    u19: AssignableMeta[T, F, EF] => AssignmentAction[U19],
    u20: AssignableMeta[T, F, EF] => AssignmentAction[U20],
    u21: AssignableMeta[T, F, EF] => AssignmentAction[U21],
    u22: AssignableMeta[T, F, EF] => AssignmentAction[U22],
    u23: AssignableMeta[T, F, EF] => AssignmentAction[U23],
    u24: AssignableMeta[T, F, EF] => AssignmentAction[U24],
    u25: AssignableMeta[T, F, EF] => AssignmentAction[U25]
  ): UpdateCommand[
    U1 ~ U2 ~ U3 ~ U4 ~ U5 ~ U6 ~ U7 ~ U8 ~ U9 ~ U10 ~ U11 ~ U12 ~ U13 ~ U14 ~ U15 ~ U16 ~ U17 ~ U18 ~ U19 ~ U20 ~ U21 ~ U22 ~ U23 ~ U24 ~ U25
  ] = update(
    u1(assignableMeta) <+> u2(assignableMeta) <+> u3(assignableMeta) <+> u4(assignableMeta) <+> u5(
      assignableMeta
    ) <+> u6(assignableMeta) <+> u7(assignableMeta) <+> u8(assignableMeta) <+> u9(assignableMeta) <+> u10(
      assignableMeta
    ) <+> u11(assignableMeta) <+> u12(assignableMeta) <+> u13(assignableMeta) <+> u14(assignableMeta) <+> u15(
      assignableMeta
    ) <+> u16(assignableMeta) <+> u17(assignableMeta) <+> u18(assignableMeta) <+> u19(assignableMeta) <+> u20(
      assignableMeta
    ) <+> u21(assignableMeta) <+> u22(assignableMeta) <+> u23(assignableMeta) <+> u24(assignableMeta) <+> u25(
      assignableMeta
    )
  )

  protected[this] def update[U](updates: AssignmentAction[U]): UpdateCommand[U]
}
