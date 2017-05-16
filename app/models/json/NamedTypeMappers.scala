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

package models.json

import java.util.UUID

import ch.datascience.graph.NamespaceAndName
import ch.datascience.graph.types.persistence.model.relational.{RowNamedType, RowPropertyKey}
import ch.datascience.graph.types.{Cardinality, DataType}
import ch.datascience.graph.types.persistence.model.{GraphDomain, NamedType, PropertyKey}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by johann on 16/05/17.
  */
object NamedTypeMappers {

  def namedTypeWrites: Writes[NamedType] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "graphDomain").write[GraphDomain] and
      (JsPath \ "name").write[String] and
      (JsPath \ "super_types").write[Map[NamespaceAndName, RowNamedType]](superTypesWrites) and
      (JsPath \ "properties").write[Map[NamespaceAndName, PropertyKey]](propertiesWrites2)
    //      (JsPath \ "properties").write[Map[NamespaceAndName, RowPropertyKey]](propertiesWrites)
  )(unlift(NamedType.unapply))

  def rowNamedTypeWrites: Writes[RowNamedType] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "graphDomainId").write[UUID] and
      (JsPath \ "name").write[String]
  )(unlift(RowNamedType.unapply))

  def rowPropertyKeyWrites: Writes[RowPropertyKey] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "graphDomainId").write[UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "datatype").write[DataType] and
      (JsPath \ "cardinality").write[Cardinality]
  )(unlift(RowPropertyKey.unapply))

  def superTypesWrites: Writes[Map[NamespaceAndName, RowNamedType]] = new Writes[Map[NamespaceAndName, RowNamedType]] {
    private[this] implicit lazy val rowNamedTypeWritesV: Writes[RowNamedType] = rowNamedTypeWrites
    private[this] def mapWrites = implicitly[Writes[Map[String, RowNamedType]]]
    def writes(map: Map[NamespaceAndName, RowNamedType]): JsValue = mapWrites.writes(map.map({ case (k,v) => k.asString -> v }))
  }

  def propertiesWrites: Writes[Map[NamespaceAndName, RowPropertyKey]] = new Writes[Map[NamespaceAndName, RowPropertyKey]] {
    private[this] implicit lazy val rowPropertyKeyWritesV: Writes[RowPropertyKey] = rowPropertyKeyWrites
    private[this] def mapWrites = implicitly[Writes[Map[String, RowPropertyKey]]]
    def writes(map: Map[NamespaceAndName, RowPropertyKey]): JsValue = mapWrites.writes(map.map({ case (k,v) => k.asString -> v }))
  }

  def propertiesWrites2: Writes[Map[NamespaceAndName, PropertyKey]] = new Writes[Map[NamespaceAndName, PropertyKey]] {
    private[this] implicit lazy val propertyKeyWritesV: Writes[PropertyKey] = PropertyKeyMappers.propertyKeyWrites
    private[this] def mapWrites2 = implicitly[Writes[Map[String, PropertyKey]]](Writes.mapWrites(propertyKeyWritesV))
    def writes(map: Map[NamespaceAndName, PropertyKey]): JsValue = mapWrites2.writes(map.map({ case (k,v) => k.asString -> v }))
  }

}
