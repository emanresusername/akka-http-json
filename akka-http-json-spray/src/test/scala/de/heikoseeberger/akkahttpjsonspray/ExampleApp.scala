/*
 * Copyright 2015 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.akkahttpjsonspray

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.server.Directives
import akka.stream.{ ActorFlowMaterializer, FlowMaterializer }
import scala.concurrent.ExecutionContext
import scala.io.StdIn
import spray.json.DefaultJsonProtocol

object ExampleApp extends DefaultJsonProtocol {

  case class Foo(bar: String)
  implicit val fooFormat = jsonFormat1(Foo)

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat = ActorFlowMaterializer()
    import system.dispatcher

    Http().bindAndHandle(route, "127.0.0.1", 8080)

    StdIn.readLine("Hit ENTER to exit")
    system.shutdown()
    system.awaitTermination()
  }

  def route(implicit ec: ExecutionContext, mat: FlowMaterializer) = {
    import Directives._
    import SprayJsonMarshalling._
    path("") {
      post {
        entity(as[Foo]) { foo =>
          complete {
            foo
          }
        }
      }
    }
  }
}