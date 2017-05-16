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

package ch.datascience.graph.types.persistence.orchestration

import java.util.UUID

import ch.datascience.graph.NamespaceAndName
import ch.datascience.graph.types.persistence.model.{GraphDomain, NamedType, PropertyKey}
import ch.datascience.graph.types.persistence.model.relational.{RowNamedType, RowPropertyKey}

import language.{higherKinds, reflectiveCalls}
import scala.concurrent.Future

/**
  * Created by johann on 15/05/17.
  */
trait NamedTypeComponent {
  this: ExecutionComponent with DatabaseComponent with GraphDomainComponent with PropertyKeyComponent =>

  import profile.api._
  import dal._

  object namedTypes {

    def all(): Future[Seq[NamedType]] = dal.namedTypes.runNamedTypeQuery

    def findById(id: UUID): Future[Option[NamedType]] = {
      val futureSeq = dal.namedTypes.findRowById(id).extract.runNamedTypeQuery
      for {
        seq <- futureSeq
      } yield seq.headOption
    }

    def findByNamespaceAndName(key: NamespaceAndName): Future[Option[NamedType]] = {
      findByNamespaceAndName(key.namespace, key.name)
    }

    def findByNamespaceAndName(namespace: String, name: String): Future[Option[NamedType]] = {
      val futureSeq = dal.namedTypes.findRowByNamespaceAndName(namespace, name).extract.runNamedTypeQuery
      for {
        seq <- futureSeq
      } yield seq.headOption
    }

    def createNamedType(
      graphDomain: GraphDomain,
      name: String,
      superTypesMap: Map[NamespaceAndName, RowNamedType],
//      propertiesMap: Map[NamespaceAndName, RowPropertyKey]
      propertiesMap: Map[NamespaceAndName, PropertyKey]
    ): Future[NamedType] = {
      val namedType = NamedType(UUID.randomUUID(), graphDomain, name, superTypesMap, propertiesMap)
      val insertNamedType = dal.namedTypes add namedType
      db.run(insertNamedType) map { _ => namedType }
    }

    def createNamedType(
      namespace: String,
      name: String,
      superTypes: Set[NamespaceAndName],
      properties: Set[NamespaceAndName]
    ): Future[NamedType] = {
      val selectGraphDomain = graphDomains.findByNamespace(namespace).map(_.get)
      val linearized = linearize(superTypes, properties)

      val joinedFuture = for {
        graphDomain <- selectGraphDomain
        (superTypesMap, propertiesMap) <- linearized
      } yield (graphDomain, superTypesMap, propertiesMap)

      joinedFuture flatMap { case (graphDomain, superTypesMap, propertiesMap) => createNamedType(graphDomain, name, superTypesMap, propertiesMap) }
    }

//    def linearize(superTypes: Set[NamespaceAndName], properties: Set[NamespaceAndName]): Future[(Map[NamespaceAndName, RowNamedType], Map[NamespaceAndName, RowPropertyKey])] = {
    def linearize(superTypes: Set[NamespaceAndName], properties: Set[NamespaceAndName]): Future[(Map[NamespaceAndName, RowNamedType], Map[NamespaceAndName, PropertyKey])] = {
      val futureSuperTypes = for {
        iterable <- Future.traverse(superTypes.toIterable)(this.findByNamespaceAndName)
      } yield for {
        opt <- iterable
        x <- opt
      } yield x

      val futureProperties = for {
        iterable <- Future.traverse(properties.toIterable)(propertyKeys.findByNamespaceAndName)
      } yield for {
        opt <- iterable
        x <- opt
      } yield x

      val linearizedSuperTypes = for {
        superTypes <- futureSuperTypes
        directSuperTypes = for {
          superType <- superTypes
        } yield superType.key -> superType.toRow
        indirectSuperTypes = for {
          superType <- superTypes
          (key, value) <- superType.superTypesMap
        } yield key -> value
      } yield (directSuperTypes ++ indirectSuperTypes).toMap

      val linearizedProperties = for {
        properties <- futureProperties
        superTypes <- futureSuperTypes
        directProperties = for {
          property <- properties
        } yield property.key -> property
        impliedProperties = for {
          superType <- superTypes
          (key, value) <- superType.propertiesMap
        } yield key -> value
      } yield (directProperties ++ impliedProperties).toMap

      for {
        res1 <- linearizedSuperTypes
        res2 <- linearizedProperties
      } yield (res1, res2)
    }

  }

}
