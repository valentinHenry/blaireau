// Written by Valentin "Firiath" Henry
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import blaireau.dsl.assignment.MetaFieldAssignmentSyntax
import blaireau.dsl.filtering.MetaFieldBooleanSyntax
import blaireau.metas.instances.AllMetaInstances

package object dsl extends AllMetaInstances with MetaFieldBooleanSyntax with MetaFieldAssignmentSyntax {}
