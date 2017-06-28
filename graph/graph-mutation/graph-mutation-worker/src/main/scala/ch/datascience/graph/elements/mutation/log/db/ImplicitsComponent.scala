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

package ch.datascience.graph.elements.mutation.log.db

import java.time.Instant
import java.sql.Timestamp
import play.api.libs.json.{JsValue, Json}

/**
  * Created by johann on 07/06/17.
  */
trait ImplicitsComponent { this: JdbcProfileComponent =>

  import profile.api._

  implicit val jsonColumnType: BaseColumnType[JsValue] =
    MappedColumnType.base[JsValue, String](_.toString(), Json.parse)

  implicit val customTimestampColumnType: BaseColumnType[Instant] =
    MappedColumnType.base[Instant, Long](_.toEpochMilli, Instant.ofEpochMilli)

}
