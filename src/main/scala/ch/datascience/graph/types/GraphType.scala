/*
 * Copyright 2017 - Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.datascience.graph.types

import ch.datascience.graph.HasKey

/**
  * Base trait for graph types
  */
sealed trait GraphType {

  /**
    * Subtype check
    *
    * @param t the other type
    * @return true iff this is a subtype of t
    */
  def <<(t: GraphType): Boolean

  /**
    * Supertype check
    *
    * @param t the other type
    * @return true iff this is a supertype of t
    */
  def >>(t: GraphType): Boolean = t << this

}


/**
  * Bottom type
  *
  * There should be no instance of Bottom.
  */
case object Bottom extends GraphType {

  def <<(t: GraphType): Boolean = true

}

/**
  * Base trait for record types
  *
  * Only property keys are taken into account, not the properties themselves.
  *
  * @tparam PropKey type of property keys
  */
trait RecordType[PropKey] extends GraphType {

  def properties: Set[PropKey]

  final def <<(t: GraphType): Boolean = t match {
    case rt: RecordType[PropKey] => rt.properties subsetOf this.properties
    case _                       => false
  }

}

/**
  * Base trait for named types
  *
  * Like [[RecordType]], only keys are used for named record types and properties.
  *
  * @tparam TypeKey type of named type keys
  * @tparam PropKey type of property keys
  */
trait NamedType[TypeKey, PropKey] extends GraphType with HasKey[TypeKey] {

  def superTypes: Set[TypeKey]

  def properties: Set[PropKey]

  def like: RecordType[PropKey]

  final def <<(t: GraphType): Boolean = t match {
    case nt: NamedType[TypeKey, PropKey] => (superTypes + this.key) contains nt.key
    case rt: RecordType[PropKey]         => rt.properties subsetOf this.properties
    case _                               => false
  }
}

