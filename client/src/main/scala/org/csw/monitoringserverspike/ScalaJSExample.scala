package org.csw.monitoringserverspike

import outwatch.Sink
import outwatch.dom._
import rxscalajs.Observable

object ScalaJSExample extends App {

  def headerComponent(title: String, hideSecond: Boolean = false): VNode = {
    div(
      h1(title),
      h2(hidden := hideSecond, "This is the second title")
    )
  }

  val introComponent: Boolean ⇒ VNode =
    headerComponent("Trying component", _: Boolean)

  def nameListComponent(nameLists: Observable[List[VNode]]): VNode = {
    div(
      span(
        children <-- nameLists
      )
    )
  }

  private val numbers: Observable[List[VNode]] =
    Observable.just(List("1", "2", "3", "4").map(x ⇒ li(x)))

  def inputComponent(labelTest: String, textValues: Sink[String]): VNode = {
    div(
      label(labelTest),
      input(inputString --> textValues)
    )
  }

  def buttonComponent(labelText: String, testValues: Sink[String]): VNode = {
    div(
      label(labelText),
      button(click(labelText) --> testValues)
    )
  }

  def personComponent(labelText: String, testValues: Sink[String]): VNode = {
    val firstNames: Handler[String] = createStringHandler("")
    val lastNames: Handler[String] = createStringHandler("")

    val fullName: Observable[String] =
      firstNames.combineLatestWith(lastNames)((first, last) ⇒ s"$first $last")

    val disableEvents: Observable[Boolean] = fullName.map(_.length < 10)

    val clearTexts: Handler[String] = createStringHandler("")

    div(
      label(labelText),
      input(inputString --> firstNames, value <-- clearTexts),
      input(inputString --> lastNames, value <-- clearTexts),
      button(
        click(fullName) --> testValues,
        click("") --> clearTexts,
        disabled <-- disableEvents,
        "Submit"
      )

    )
  }

  def parent(): VNode = {
    val names = createStringHandler()
    div(
      personComponent("form", names),
      h2("Hello2", child <-- names)
    )
  }

  OutWatch.render("#app", parent())
}
