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

//package ch.datascience.typesystem
//
//import ch.datascience.typesystem.graphdb.GraphAccessLayer
//import org.janusgraph.core.JanusGraphFactory
//
//import scala.concurrent.Await
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration.Duration
//import scala.util.{Failure, Success}
//
///**
//  * Created by johann on 21/03/17.
//  */
//object App2 {
//
//  def main(args: Array[String]): Unit = {
//
//    val graph = JanusGraphFactory.open("./conf/janusgraph-berkeleyje-es.properties")
//
//    try {
//
//      val gal = new GraphAccessLayer(graph)
//
//      val f1 = gal.run(for { pk <- gal.getPropertyKey("foo") } yield (pk.name(), pk.cardinality(), pk.dataType())).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f1, Duration.Inf)
//
//      val f2 = gal.run({mgmt => mgmt.containsPropertyKey("foo")}).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f2, Duration.Inf)
//
//      val f3 = gal.run(gal.addPropertyKey("foo", DataType.Long, Cardinality.Single)).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f3, Duration.Inf)
//
//      val f3_ = gal.run(gal.addPropertyKey("bar", DataType.String, Cardinality.Single)).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f3_, Duration.Inf)
//
//      val f4 = gal.run(for { pk1 <- gal.getPropertyKey("foo"); pk2 <- gal.getPropertyKey("bar") } yield (pk1.name(), pk2.name())).map(println).recover({case e => println(s"Error: $e")})
//      Await.ready(f4, Duration.Inf)
//
//    } finally {
//      graph.close()
//    }
//
//
//  }
//
//}
