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

package ch.datascience.graph.values.json

import ch.datascience.graph.types.DataType
import ch.datascience.graph.types.json.DataTypeReads
import ch.datascience.graph.values.BoxedValue
import play.api.libs.json._

/**
  * Created by johann on 24/05/17.
  */
object BoxedValueReads extends Reads[BoxedValue] {

  def reads(json: JsValue): JsResult[BoxedValue] = {
    val result = for {
      dataType <- dataTypeReads.reads(json)
      value <- valueReads(dataType).reads(json)
    } yield value

    // Need to repath result
    result match {
      case JsSuccess(x, _) => JsSuccess(x)
      case _ => result
    }
  }

  private[this] lazy val dataTypeReads: Reads[DataType] = (JsPath \ "data_type").read[DataType](DataTypeReads)

  private[this] def valueReads(dataType: DataType): Reads[BoxedValue] = (JsPath \ "value").read[BoxedValue](ValueReads(dataType))

}
