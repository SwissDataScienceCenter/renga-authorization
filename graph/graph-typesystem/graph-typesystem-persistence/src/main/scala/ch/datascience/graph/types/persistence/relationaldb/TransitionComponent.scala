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

package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.types.persistence.model.{EntityState, Entity, State, Transition}
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

/**
  * Created by johann on 20/03/17.
  */
trait TransitionComponent { this: JdbcProfileComponent with SchemasComponent with ImplicitsComponent with StateComponent with EntityComponent =>

  import profile.api._

  class Transitions(tag: Tag) extends Table[Transition](tag, "TRANSITIONS") {

    // Columns
    def entityId: Rep[UUID] = column[UUID]("ENTITY_UUID")

    def from: Rep[Long] = column[Long]("FROM")

    def toState: Rep[EntityState] = column[EntityState]("TO_STATE")

    def toTimestamp: Rep[Instant] = column[Instant]("TO_TIMESTAMP")

    // Indexes
    def pk: PrimaryKey = primaryKey("IDX_TRANSITIONS", (entityId, from))

    // Foreign keys
    def entity: ForeignKeyQuery[Entities, Entity] =
      foreignKey("TRANSITIONS_FK_ENTITIES", entityId, entities)(_.id)

    def fromState: ForeignKeyQuery[States, State] =
      foreignKey("TRANSITIONS_FK_STATES", from, states)(_.id)

    // *
    def * : ProvenShape[Transition] = (entityId, from, toState, toTimestamp) <> (Transition.tupled, Transition.unapply)

  }

  object transitions extends TableQuery(new Transitions(_))

  _schemas += transitions.schema

}
