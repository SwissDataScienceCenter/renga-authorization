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

import ch.datascience.graph.elements._

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
MetaKey,
+MetaValue,
+MetaProp <: PersistedRecordProperty[MetaKey, MetaValue, MetaProp],
+PropId,
+Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop] with PersistedMultiRecordProperty[PropId, Key, Value, Prop]
] extends Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop]
  with PersistedElement[VertexPath[VertexRef[Id]]]
  with HasId[VertexRef[Id]] {

  final def path: VertexPath[VertexRef[Id]] = VertexPath(id)

}

trait PersistedEdge[
+Id,
Key,
+Value,
+EdgeProp <: PersistedRecordProperty[Key, Value, EdgeProp],
+VertexId
] extends Edge[Key, Value, EdgeProp, VertexId]
  with PersistedElement[EdgePath[VertexId, Id]]
  with HasId[Id]{

  final def path: EdgePath[VertexId, Id] = EdgePath(from, id)

}

sealed trait PersistedProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends Property[Key, Value, This]
    with PersistedElement[PropertyPath] { this: This =>
}

trait PersistedRecordProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PersistedProperty[Key, Value, This]
    with PersistedElement[PropertyPathFromRecord[Key]] { this: This =>

  def parent: Path

  final def path: PropertyPathFromRecord[Key] = PropertyPathFromRecord(parent, key)

}

trait PersistedMultiRecordProperty[+Id, +Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PersistedProperty[Key, Value, This]
    with PersistedElement[PropertyPathFromMultiRecord[Id]]
    with HasId[Id] { this: This =>

  def parent: Path

  final def path: PropertyPathFromMultiRecord[Id] = PropertyPathFromMultiRecord(parent, id)

}


sealed trait NewProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends Property[Key, Value, This]
    with NewElement { this: This =>
}

trait NewRecordProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends NewProperty[Key, Value, This]
    with HasPath[Path]
    with NewElement { this: This =>

  def parent: Path

  final def path: PropertyPathFromRecord[Key] = PropertyPathFromRecord(parent, key)

}

trait NewMultiRecordProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends NewProperty[Key, Value, This]
    with HasPath[Path]
    with NewElement { this: This =>

  type TempId = Int

  def tempId: TempId

  def parent: Path

  final def path: PropertyPathFromMultiRecord[TempId] = PropertyPathFromMultiRecord(parent, tempId)

}


trait NewVertex[
TypeId,
Key,
+Value,
MetaKey,
+MetaValue,
+MetaProp <: Property[MetaKey, MetaValue, MetaProp],
+Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop]
] extends Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop]
  with NewElement {

  type TempId = Int

  def tempId: TempId

}

trait NewEdge[
+Id,
Key,
+Value,
+EdgeProp <: Property[Key, Value, EdgeProp]
] extends Edge[Key, Value, EdgeProp, Either[Id, NewVertex[Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing]#TempId]]
  with NewElement {

  type TempId = Int

  def tempId: TempId
}
