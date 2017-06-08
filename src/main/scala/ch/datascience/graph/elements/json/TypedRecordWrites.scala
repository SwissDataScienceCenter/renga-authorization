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

package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Property, Record, TypedRecord}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 31/05/17.
  */
class TypedRecordWrites[P <: Property : Writes] extends Writes[TypedRecord { type Prop <: P }] {

  def writes(record: TypedRecord {type Prop <: P}): JsValue = self.writes(record)

  private[this] lazy val self: Writes[TypedRecord { type Prop <: P }] = (
    (JsPath \ "types").write[Iterable[TypedRecord#TypeId]] and
      JsPath.write[Record { type Prop <: P }](recordWrites)
    ) { record => (record.types, record) }

  private[this] lazy val recordWrites = new RecordWrites[P]

  private[this] implicit lazy val typeWrites: Writes[TypedRecord#TypeId] = TypeIdFormat

}
