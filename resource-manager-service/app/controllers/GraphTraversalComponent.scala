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

import ch.datascience.graph.execution.GraphExecutionContext
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import persistence.graph.{GraphExecutionContextProvider, JanusGraphTraversalSourceProvider}
import play.api.mvc.Controller

/**
  * Created by johann on 13/06/17.
  */
trait GraphTraversalComponent { this: Controller =>

  protected def graphExecutionContextProvider: GraphExecutionContextProvider

  implicit protected def graphExecutionContext: GraphExecutionContext = graphExecutionContextProvider.get

  protected def janusGraphTraversalSourceProvider: JanusGraphTraversalSourceProvider

  implicit protected def graphTraversalSource: GraphTraversalSource = janusGraphTraversalSourceProvider.get

}
