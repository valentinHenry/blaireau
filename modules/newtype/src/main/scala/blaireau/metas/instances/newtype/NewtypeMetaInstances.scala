// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances.newtype

import blaireau.metas.MetaS
import io.estatico.newtype._

trait NewtypeMetaInstances {
  implicit def coercibleMeta[A, B](implicit c: Coercible[A, B], m: MetaS[B]): MetaS[A] =
    m.imap(_.asInstanceOf[A])(c(_))
}
