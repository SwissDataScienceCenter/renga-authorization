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

package ch.datascience.graph.elements.persistence

import ch.datascience.graph.bases.HasId
import ch.datascience.graph.elements._
import ch.datascience.graph.elements.persistence.impl.ImplPersistedRecordProperty

/**
  * Created by johann on 11/05/17.
  */
sealed trait NewOrPersistedElement extends Element

sealed trait PersistedElement[+P <: Path] extends NewOrPersistedElement with HasPath[P]

sealed trait NewElement extends NewOrPersistedElement

trait PersistedVertex[
+Id,
TypeId,
Key,
+Value,
+MetaValue,
+MetaProp <: PersistedRecordProperty[Key, MetaValue],
+PropId,
+Prop <: RichProperty[Key, Value, MetaValue, MetaProp] with PersistedMultiRecordProperty[PropId, Key, Value]
] extends Vertex[TypeId, Key, Value, MetaValue, MetaProp, Prop]
  with PersistedElement[VertexPath[Id]]
  with HasId[Id] {

  final def path: VertexPath[Id] = VertexPath(id)

}

trait PersistedEdge[
+Id,
Key,
+Value,
+EdgeProp <: PersistedRecordProperty[Key, Value],
+VertexId
] extends Edge[Key, Value, EdgeProp, VertexId]
  with PersistedElement[EdgePath[VertexId, Id]]
  with HasId[Id]{

  final def path: EdgePath[VertexId, Id] = EdgePath(from, id)

}

sealed trait PersistedProperty[+Key, +Value]
  extends Property[Key, Value]
    with PersistedElement[PropertyPath] {

  def parent: Path

}

trait PersistedRecordProperty[+Key, +Value]
  extends PersistedProperty[Key, Value]
    with PersistedElement[PropertyPathFromRecord[Key]] {

  final def path: PropertyPathFromRecord[Key] = PropertyPathFromRecord(parent, key)

}

trait PersistedMultiRecordProperty[+Id, +Key, +Value]
  extends PersistedProperty[Key, Value]
    with PersistedElement[PropertyPathFromMultiRecord[Id]]
    with HasId[Id] {

  final def path: PropertyPathFromMultiRecord[Id] = PropertyPathFromMultiRecord(parent, id)

}

trait PersistedRecordRichProperty[Key, +Value, +MetaValue]
  extends PersistedRecordProperty[Key, Value]
    with RichProperty[Key, Value, MetaValue, PersistedRecordProperty[Key, MetaValue]]

trait PersistedMultiRecordRichProperty[+Id, Key, +Value, +MetaValue]
  extends PersistedMultiRecordProperty[Id, Key, Value]
    with RichProperty[Key, Value, MetaValue, PersistedRecordProperty[Key, MetaValue]]


sealed trait NewProperty[+Key, +Value]
  extends Property[Key, Value]
    with NewElement

trait NewRecordProperty[+Key, +Value]
  extends NewProperty[Key, Value]
    with HasPath[Path]
    with NewElement {

  def parent: Path

  final def path: PropertyPathFromRecord[Key] = PropertyPathFromRecord(parent, key)

}

trait NewMultiRecordProperty[+Key, +Value]
  extends NewProperty[Key, Value]
    with HasPath[Path]
    with NewElement {

  type TempId = Int

  def tempId: TempId

  def parent: Path

  final def path: PropertyPathFromMultiRecord[TempId] = PropertyPathFromMultiRecord(parent, tempId)

}


trait NewVertex[
TypeId,
Key,
+Value,
+MetaValue,
+MetaProp <: Property[Key, MetaValue],
+Prop <: RichProperty[Key, Value, MetaValue, MetaProp]
] extends Vertex[TypeId, Key, Value, MetaValue, MetaProp, Prop]
  with NewElement {

  type TempId = Int

  def tempId: TempId

}

trait NewEdge[
+Id,
Key,
+Value,
+EdgeProp <: Property[Key, Value]
] extends Edge[Key, Value, EdgeProp, Either[Id, NewVertex[Nothing, Nothing, Nothing, Nothing, Nothing, Nothing]#TempId]]
  with NewElement {

  type TempId = Int

  def tempId: TempId
}
