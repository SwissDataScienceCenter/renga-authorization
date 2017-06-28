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

import models.WriteResourceRequest
import play.api.libs.functional.syntax._
import play.api.libs.json._


object WriteResourceRequestMappers {

  def writeResourceRequestReads: Reads[WriteResourceRequest] = (
      (JsPath \ "app_id").readNullable[Long] and
      (JsPath \ "target").read[Either[String, Long]]
    )(WriteResourceRequest.apply _)

  private[this] implicit lazy val targetReader: Reads[Either[String, Long]] = (JsPath \ "type").read[String].flatMap {
    case "filename" => (JsPath \ "filename").read[String].map(s => Left(s))
    case "resource" => (JsPath \ "resource_id").read[Long].map(l => Right(l))
    case t => Reads { json => JsError(s"Usupported type $t") }
  }
}