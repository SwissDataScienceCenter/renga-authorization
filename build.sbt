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

organization := "ch.datascience"
name := "graph-typesystem-persistence"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

resolvers += DefaultMavenRepository
//resolvers += "SDSC Snapshots" at "https://internal.datascience.ch:8081/nexus/content/repositories/snapshots/"
resolvers += "jitpack" at "https://jitpack.io"
resolvers += "Oracle Released Java Packages" at "http://download.oracle.com/maven"

lazy val slick_version = "3.2.0"
lazy val play_slick_version = "2.1.0"
lazy val janusgraph_version = "0.1.0"
//lazy val h2_version = "1.4.193"
//lazy val slf4j_version = "1.7.24"

//libraryDependencies += "ch.datascience" %% "graph-type-utils" % version.value
libraryDependencies += "com.typesafe.slick" %% "slick" % slick_version
libraryDependencies += "com.typesafe.play" %% "play-slick" % play_slick_version
libraryDependencies += "org.janusgraph" % "janusgraph-core" % janusgraph_version
//libraryDependencies += "org.janusgraph" % "janusgraph-berkeleyje" % janusgraph_version
//libraryDependencies += "org.janusgraph" % "janusgraph-es" % janusgraph_version
//libraryDependencies += "com.h2database" % "h2" % h2_version
//libraryDependencies += "org.slf4j" % "slf4j-nop" % slf4j_version

lazy val h2_version = "1.4.193"
lazy val scalatest_version = "3.0.1"

libraryDependencies += "com.h2database" % "h2" % h2_version % Test
libraryDependencies += "org.scalatest" %% "scalatest" % scalatest_version % Test

logBuffered in Test := false
parallelExecution in Test := false
