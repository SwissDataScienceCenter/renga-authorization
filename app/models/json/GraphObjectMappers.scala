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

import ch.datascience.typesystem.model.GraphObject
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by johann on 26/04/17.
  */
object GraphObjectMappers {

  def valueWrite: Writes[Any] = new Writes[Any] {
    override def writes(value: Any): JsValue = value match {
      case x: String => JsString(x)
      case x: Char => JsObject(Seq(("type", JsString("char")), ("value", JsString(x.toString))))
      case x: Boolean => JsBoolean(x)
      case x: Short => JsObject(Seq(("type", JsString("short")), ("value", JsNumber(BigDecimal(x)))))
      case x: Int => JsObject(Seq(("type", JsString("int")), ("value", JsNumber(BigDecimal(x)))))
      case x: Long => JsNumber(BigDecimal(x))
      case x: Float => JsObject(Seq(("type", JsString("float")), ("value", JsNumber(BigDecimal(x.toDouble)))))
      case x: Double => JsNumber(BigDecimal(x))
      case _ => throw new IllegalArgumentException(s"Value unsupported: $value")
    }
  }

  def valuesWrites: Writes[Any] = new Writes[Any] {
    override def writes(values: Any): JsValue = values match {
      case seq: Seq[_] => JsArray(seq.map(x => Json.toJson(x)(valueWrite)))
      case set: Set[_] => JsArray(set.map(x => Json.toJson(x)(valueWrite)).toSeq)
      case _ => Json.toJson(values)(valueWrite)
    }
  }

  def propertiesWrites: Writes[Map[String, Any]] = new Writes[Map[String, Any]] {
    override def writes(properties: Map[String, Any]): JsValue = {
      val fields = for {
        (key, value) <- properties
      } yield key -> Json.toJson(value)(valuesWrites)
      JsObject(fields)
    }
  }

  def graphObjectWrites: Writes[GraphObject] = (
    (JsPath \ "types").write[Set[String]] and
      (JsPath \ "properties").write[Map[String, Any]](propertiesWrites)
    ) (unlift(GraphObject.unapply))

  //TODO: make it properly the reverse of graphObjectWrites
  def graphObjectReads: Reads[GraphObject] = (
    (JsPath \ "types").read[Set[String]] and
      (JsPath \ "properties").read[JsObject]
  )((types, properties) => GraphObject(types, properties.fields.toMap))

}
