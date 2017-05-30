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

package ch.datascience.graph.elements.detached

import ch.datascience.graph.elements.detached.impl.ImplDetachedEdge
import ch.datascience.graph.elements.{Edge, Vertex}

/**
  * Created by johann on 29/05/17.
  */
trait DetachedEdge extends Edge {

  final type VertexReference = Vertex

  final type Prop = DetachedProperty

}

object DetachedEdge {

  def apply(
    from: DetachedEdge#VertexReference,
    to: DetachedEdge#VertexReference,
    types: Set[DetachedEdge#TypeId],
    properties: DetachedEdge#Properties
  ): DetachedEdge = ImplDetachedEdge(from, to, types, properties)

  def unapply(edge: DetachedEdge): Option[(DetachedEdge#VertexReference, DetachedEdge#VertexReference, Set[DetachedEdge#TypeId], DetachedEdge#Properties)] = {
    if (edge eq null)
      None
    else
      Some(edge.from, edge.to, edge.types, edge.properties)
  }

}
