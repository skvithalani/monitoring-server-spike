package org.csw.monitoringserverspike

import java.nio.file.Files

import akka.http.scaladsl.server.Directives
import org.csw.monitoringserverspike.shared.SharedMessages

class WebService() extends Directives {

  val route = {
    pathSingleSlash {
      getFromResource("index.html")
    }
  } ~
    pathPrefix("assets" / Remaining) { file =>
      // optionally compresses the response with Gzip or Deflate
      // if the client accepts compressed responses
      encodeResponse {
        getFromResource("public/" + file)
      }
    } ~ path("fastOp.js") {
    getFromFile("../client/target/scala-2.12/scalajs-bundler/main/client-fastopt-bundle.js")
  }
}
