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

package ch.datascience.graph.typecheck

import ch.datascience.graph.elements.{BoxedOrValidValue, Properties, Property}
import ch.datascience.graph.types.PropertyKey

import scala.util.{Failure, Try}

/**
  * Created by johann on 01/05/17.
  */
trait HasPropertiesChecker[Key, Value, Prop <: Property[Key, Value, Prop]] { this: PropertyChecker[Key, Value, Prop] =>

  def checkProperties(properties: Properties[Key, Value, Prop], definitions: Map[Key, PropertyKey[Key]])(implicit e: BoxedOrValidValue[Value]): ValidationResult[HasPropertiesChecked[Key]] = {
    val checkedProperties = for {
      (key, prop) <- properties
    } yield key -> checkProperty(prop, definitions)(e)

    val invalidProperties = checkedProperties.values.flatMap(_.left.toOption)

    if (invalidProperties.isEmpty) {
      val validProperties = for {
        (key, check) <- checkedProperties
        v <- check.right.toOption
      } yield key -> v.propertyKey
      Right(Result(validProperties))
    }else
      Left(MultipleErrors(invalidProperties.toSeq))
  }

  private[this] case class Result(propertyKeys: Map[Key, PropertyKey[Key]]) extends HasPropertiesChecked[Key]

}
