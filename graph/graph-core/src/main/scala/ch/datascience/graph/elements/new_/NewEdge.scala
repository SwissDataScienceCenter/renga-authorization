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

package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.Edge
import ch.datascience.graph.elements.detached.DetachedProperty
import ch.datascience.graph.elements.new_.impl.ImplNewEdge
import ch.datascience.graph.elements.persisted.PersistedVertex

/**
  * Created by johann on 29/05/17.
  */
trait NewEdge extends Edge with NewElement {

  final type PersistedVertexType = PersistedVertex

  final type NewVertexType = NewVertex

  final type VertexReference = Either[NewVertexType#TempId, PersistedVertexType#Id]

  final type Prop = DetachedProperty

}

object NewEdge {

  def apply(
    label: NewEdge#Label,
    from: NewEdge#VertexReference,
    to: NewEdge#VertexReference,
    properties: NewEdge#Properties
  ): NewEdge = ImplNewEdge(label, from, to, properties)

  def unapply(edge: NewEdge): Option[(NewEdge#Label, NewEdge#VertexReference, NewEdge#VertexReference, NewEdge#Properties)] = {
    if (edge eq null)
      None
    else
      Some(edge.label, edge.from, edge.to, edge.properties)
  }

}
