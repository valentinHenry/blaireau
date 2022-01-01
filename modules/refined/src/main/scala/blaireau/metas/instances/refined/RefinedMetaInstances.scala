// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau.metas.instances.refined

import blaireau.metas.{Meta, MetaS}
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV

trait RefinedMetaInstances {
  implicit def refinedMeta[R, V, P, CF](implicit
    meta: MetaS[V],
    validator: Validate[V, P]
  ): MetaS[V Refined P] = {
    val codec = meta.codec.eimap(refineV[P](_))(_.value)

    Meta.of(codec)
  }
}
