package org.csw.monitoringserverspike

import org.csw.monitoringserverspike.shared.SharedMessages
import outwatch.dom._

/*
import org.csw.monitoringserverspike.shared.SharedMessages
import org.scalajs.dom

object ScalaJSExample {

  def main(args: Array[String]): Unit = {
    dom.document.getElementById("scalajsShoutOut").textContent = SharedMessages.itWorks
  }
}
*/

object ScalaJSExample extends App {

    OutWatch.render("#app", h1(SharedMessages.itWorks))
}
