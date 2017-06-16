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

package ch.datascience.graph.elements.persisted

import ch.datascience.graph.Constants
import ch.datascience.graph.elements.persisted.impl.ImplPersistedVertexProperty

/**
  * Created by johann on 30/05/17.
  */
trait PersistedVertexProperty extends PersistedMultiRecordRichProperty {

  type Id = Constants.VertexPropertyId

  final type PathType = VertexPropertyPath

  final def path: VertexPropertyPath = VertexPropertyPath(parent, id)

}

object PersistedVertexProperty {

  def apply(
    id: PersistedVertexProperty#Id,
    parent: Path,
    key: PersistedVertexProperty#Key,
    value: PersistedVertexProperty#Value,
    properties: PersistedVertexProperty#Properties
  ): PersistedVertexProperty = ImplPersistedVertexProperty(id, parent, key, value, properties)

  def unapply(prop: PersistedVertexProperty): Option[(PersistedVertexProperty#Id, Path, PersistedVertexProperty#Key, PersistedVertexProperty#Value, PersistedVertexProperty#Properties)] = {
    if (prop eq null)
      None
    else
      Some(prop.id, prop.parent, prop.key, prop.value, prop.properties)
  }

}
