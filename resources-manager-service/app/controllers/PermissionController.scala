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

package controllers
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.security.{KeyFactory, KeyPair}
import java.util.Base64
import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.SingleValue
import ch.datascience.graph.elements.detached.DetachedRichProperty
import ch.datascience.graph.elements.mutation.Mutation
import ch.datascience.graph.elements.mutation.create.{CreateEdgeOperation, CreateVertexOperation}
import ch.datascience.graph.elements.new_.{NewEdge, NewVertex}
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.values.StringValue
import clients.GraphClient
import models.{ReadResourceRequest, WriteResourceRequest}
import org.pac4j.core.profile.{CommonProfile, ProfileManager}

import scala.collection.JavaConversions._
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.PlayWebContext
import org.pac4j.play.store.PlaySessionStore
import models.json._
import scala.concurrent.duration._
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BodyParsers, Controller, RequestHeader}

import scala.concurrent.{Await, Future}

/**
  * Created by jeberle on 25.04.17.
  */
@Singleton
class PermissionController @Inject()(config: play.api.Configuration, val playSessionStore: PlaySessionStore, wsclient: WSClient) extends Controller with JsonComponent{

  implicit val ws: WSClient = wsclient
  implicit lazy val host: String = config
    .getString("graph.mutation.service.host")
    .getOrElse("http://localhost:9000/api/mutation/")

  private def getProfiles(implicit request: RequestHeader): List[CommonProfile] = {
    val webContext = new PlayWebContext(request, playSessionStore)
    val profileManager = new ProfileManager[CommonProfile](webContext)
    val profiles = profileManager.getAll(true)
    asScalaBuffer(profiles).toList
  }

  private def getGenerator = {
    val public_key = Base64.getDecoder.decode(config.getString("key.resource-manager.public").get)
    val private_key = Base64.getDecoder.decode(config.getString("key.resource-manager.private").get)
    val public_spec = new X509EncodedKeySpec(public_key)
    val private_spec = new PKCS8EncodedKeySpec(private_key)
    val kf = KeyFactory.getInstance("RSA")
    val key_pair = new KeyPair(kf.generatePublic(public_spec), kf.generatePrivate(private_spec))
    val signConfig = new RSASignatureConfiguration(key_pair)

    new JwtGenerator(signConfig)
  }

  def authorizeStorageRead = Action.async(bodyParseJson[ReadResourceRequest](readResourceRequestReads)) { implicit request =>
    Future {

      val profile = getProfiles(request).head
      val _request: ReadResourceRequest = request.body

      //TODO: get the graph element corresponding to the ID of the resource

      //TODO: validate its ACLs

      val token = getGenerator.generate(Map("sub" -> "StorageService", "user_id" -> profile.getId, "file_id" -> _request.resourceId.toString, "scope" -> "storage:read"))

      for (appId <- _request.appId) {
        val gc = new GraphClient
        val did = NamespaceAndName("deploy:id")
        val dimage = NamespaceAndName("deploy:image")
        val dstatus = NamespaceAndName("deploy:status")
        val dtime = NamespaceAndName("system:creation_time")
        val mut = Mutation(
          Seq(CreateEdgeOperation(NewEdge(
            NamespaceAndName("resource:read"),
            Right(appId),
            Right(_request.resourceId),
            Map()
          ))))
        gc.create(mut)
      }

      //TODO: check mutation result

      Ok("{\"permission_token\": \"" + token + "\"}")
    }
  }

  def authorizeStorageWrite = Action.async(bodyParseJson[WriteResourceRequest](writeResourceRequestReads)) { implicit request =>

      val profile = getProfiles(request).head
      val _request: WriteResourceRequest = request.body

      //TODO: get the graph element corresponding to the ID of the resource

      //TODO: validate its ACLs

      val (operation, resource_id) = _request.target match {
        case Left(filename) => (Some(CreateVertexOperation(NewVertex(
          1,
          Set(NamespaceAndName("resource:file")),
          Map(
            NamespaceAndName("resource:file_name") -> SingleValue(
              DetachedRichProperty(NamespaceAndName("resource:file_name"),
                StringValue(filename),
                Map()
              )
            )
          )
        ))), Left(1))
        case Right(id) => (None, Right(id))
      }

      val edge = _request.appId.map { appId =>

        val did = NamespaceAndName("deploy:id")
        val dimage = NamespaceAndName("deploy:image")
        val dstatus = NamespaceAndName("deploy:status")
        val dtime = NamespaceAndName("system:creation_time")
        CreateEdgeOperation(NewEdge(
            NamespaceAndName("resource:write"),
            Right(appId),
            resource_id,
            Map()
          ))
      }

      val gc = new GraphClient
      val mut = Mutation(operation.toSeq ++ edge.toSeq)

      def getVertexId(result: JsValue): Long = {
        Thread.sleep(1000)
        val status = gc.status((result \ "uuid").as[String])
        val s = Await.result(status, 5.seconds)
        if ((s \ "status").as[String].equals("completed"))
          (s \ "response" \ "event" \ "results" \ 0 \ "id").as[Long]
        else
          getVertexId(result)
      }

      gc.create(mut).map(result => {
        val res_id = _request.target.fold(_ => getVertexId(result), id => id)
        val token = getGenerator.generate(Map("sub" -> "StorageService", "user_id" -> profile.getId, "file_uuid" -> res_id.toString, "scope" -> "storage:write"))

        Ok("{\"permission_token\": \"" + token + "\", \"id\": " + res_id + " }")
      })
  }

  def authorizeComputeExecute = Action.async(BodyParsers.parse.empty) { implicit request =>
    Future {

      val profile = getProfiles(request).head
      // get the graph element corresponding to the ID of the resource

      // validate its ACLs

      val token = getGenerator.generate(Map("sub" -> "DeployService", "user_id" -> profile.getId, "scope" -> "compute:execute"))

      Ok("{\"permission_token\": \"" + token + "\"}")
    }
  }

}