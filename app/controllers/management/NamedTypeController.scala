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

package controllers.management

import java.sql.SQLException
import javax.inject.Inject

import ch.datascience.graph.NamespaceAndName
import controllers.JsonComponent
import injected.OrchestrationLayer
import play.api.libs.concurrent.Execution.Implicits._
import models.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

/**
  * Created by johann on 15/05/17.
  */
class NamedTypeController @Inject()(protected val orchestrator: OrchestrationLayer) extends Controller with JsonComponent {

  def index: Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val all = orchestrator.namedTypes.all()
//    all.map(seq => Json.toJson(seq)).map(json => Ok(json))
    all.map({seq => println(seq); Ok})
  }

  def create: Action[CreateNamedType] = Action.async(bodyParseJson[CreateNamedType](createReads)) { implicit request =>
    val createNamedType = request.body
    val future = orchestrator.namedTypes.createNamedType(createNamedType.namespace, createNamedType.name, createNamedType.superTypes, createNamedType.properties)
    future map { namedType => println(namedType); Ok /*(Json.toJson(namedType))*/ } recover {
      case e: SQLException =>
        //TODO: log exception
        Conflict // Avoids send of 500 INTERNAL ERROR if duplicate creation
    }
  }

  case class CreateNamedType (namespace: String,
                                           name: String,
                                           superTypes: Set[NamespaceAndName],
                                           properties: Set[NamespaceAndName])

  private[this] lazy val createReads: Reads[CreateNamedType] = (
    (JsPath \ "namespace").read[String](namespaceReads) and
      (JsPath \ "name").read[String](nameReads) and
      (JsPath \ "super_types").read[Seq[NamespaceAndName]] and
      (JsPath \ "properties").read[Seq[NamespaceAndName]]
    )({ (namespace, name, superTypes, properties) => CreateNamedType(namespace, name, superTypes.toSet, properties.toSet) })

}
