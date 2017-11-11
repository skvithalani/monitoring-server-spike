package org.csw.monitoringserverspike

import org.csw.monitoringserverspike.shared.SharedMessages
import outwatch.dom.helpers.InputEvent
import outwatch.dom._
import rxscalajs.Observable

object BasicOutwatch {
  private val seconds: Observable[Int] = Observable.interval(1000)
    .map(_ + 1)
    .startWith(0)

  seconds.subscribe(x ⇒ println(x))

  val app1 = div(
    h1(SharedMessages.itWorks + " dddd"),
    div("Seconds elapsed", child <-- seconds)
  )

  val toggleEvents: Handler[InputEvent] = createInputHandler()
  val app2 = div(
    label("hide view"),
    input(tpe:= "checkbox", change --> toggleEvents),
    h2(hidden  <-- toggleEvents.map(_.target.checked), "Visible")
  )

  val toggleCheckedValues: Handler[Boolean] = createBoolHandler()
  val app3 = div(
    label("hide view"),
    input(tpe:= "checkbox", inputChecked --> toggleCheckedValues),
    h2(hidden  <-- toggleCheckedValues, "Visible")
  )

  val firstNameEvents = createStringHandler()
  val lastNameEvents = createStringHandler()

  val fullNames = firstNameEvents.combineLatestWith(lastNameEvents)((first, last) ⇒ s"$first $last")
  private val app4: VNode = div(
    input(inputString --> firstNameEvents),
    input(inputString --> lastNameEvents),
    h3("Hello", child <-- fullNames)
  )
  app4

  val list = List("1", "2", "3", "4").map(x ⇒ li(x))
  val lists: Observable[List[VNode]] = Observable.just(list)
  private val app5: VNode = ul(children <-- lists)


  //  private val image: VNode = img(src := imageUrl)

  val numbers: Handler[Int] = createHandler[Int]()

  val startLists = numbers
    .map(_ / 10)
    .map(n => List.fill(n)(h1("*")))

  val app6 = div(
    input(
      tpe := "range",
      value:= 0,
      inputNumber --> numbers.redirectMap(_.toInt),
    ),
    div(
      children <-- startLists
    )
  )
}
