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

import ch.datascience.graph.types.persistence.model.{AbstractEntity, EntityState}
import ch.datascience.graph.types.persistence.model.relational.{RowAbstractEntity, RowEntity, RowState}
import slick.lifted.{CompiledFunction, ForeignKeyQuery}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 20/03/17.
  */
trait AbstractEntityComponent { this: JdbcProfileComponent with EntityComponent with StateComponent =>

  import profile.api._

  trait AbstractEntityTable[A <: RowAbstractEntity] extends Table[A] {

    // Columns
    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    // Foreign keys
    def entity: ForeignKeyQuery[Entities, RowEntity] = foreignKey(s"${this.tableName}_FK_ENTITIES", id, entities)(_.id)

  }

  trait AbstractEntitiesTableQuery[A <: RowAbstractEntity, MA <: AbstractEntity[A], TA <: Table[A] with AbstractEntityTable[A]] { this: TableQuery[TA] =>

    lazy val findRowById: CompiledFunction[Rep[UUID] => Query[TA, A, Seq], Rep[UUID], UUID, Query[TA, A, Seq], Seq[A]] =
      Compiled { (entityId: Rep[UUID]) => this.filter(_.id === entityId) }

    //def findById(id: UUID): Query[TA, MA, Seq]

    def add(entity: MA): DBIO[Unit] = {
      DBIO.seq(preInsertEntity(entity), insertEntity(entity), postInsertEntity(entity)).transactionally
    }

    protected def insertEntity(entity: MA): DBIO[Unit] = insertRow(entity.toRow)

    protected def insertRow(rowEntity: A): DBIO[Unit] = {
      DBIO.seq(this += rowEntity)
    }

    protected def preInsertEntity(entity: MA): DBIO[Unit] = preInsertRow(entity.toRow)

    protected def preInsertRow(rowEntity: A): DBIO[Unit] = {
      val insertEntity = entities += RowEntity(rowEntity.id, rowEntity.entityType)
      val insertState = states += RowState(None, rowEntity.id, EntityState.Pending, Instant.now())
      DBIO.seq(insertEntity, insertState)
    }

    protected def postInsertEntity(entity: MA): DBIO[Unit] = postInsertRow(entity.toRow)

    protected def postInsertRow(rowEntity: A): DBIO[Unit] = {
      // TODO: Commit by transitioning state
      DBIO.from(Future.successful(()))
    }

  }

}
