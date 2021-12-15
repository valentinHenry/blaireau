// Written by Valentin HENRY
//
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package blaireau

import blaireau.metas.instances.AllMetaInstances
import blaireau.dsl.syntax.{MetaFieldAssignmentSyntax, MetaFieldBooleanSyntax}

package object dsl extends AllMetaInstances with MetaFieldBooleanSyntax with MetaFieldAssignmentSyntax {}
