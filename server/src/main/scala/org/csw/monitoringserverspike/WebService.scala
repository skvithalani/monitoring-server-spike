package org.csw.monitoringserverspike

import akka.http.scaladsl.server.Directives
import org.csw.monitoringserverspike.shared.SharedMessages
import org.csw.monitoringserverspike.twirl.Implicits._

class WebService() extends Directives {

  val route = {
    pathSingleSlash {
      get {
        complete {
          org.csw.monitoringserverspike.html.index.render(SharedMessages.itWorks)
        }
      }
    } ~
      pathPrefix("assets" / Remaining) { file =>
        // optionally compresses the response with Gzip or Deflate
        // if the client accepts compressed responses
        encodeResponse {
          getFromResource("public/" + file)
        }
      }
  }
}
