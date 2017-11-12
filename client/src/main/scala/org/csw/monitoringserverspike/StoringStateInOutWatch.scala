package org.csw.monitoringserverspike

import org.csw.monitoringserverspike.StoringStateInOutWatch.TodoActions.{AddItem, RemoveItem}
import outwatch.Sink
import outwatch.dom._
import outwatch.util.Store
import rxscalajs.Observable

object StoringStateInOutWatch {
  val additions = createHandler[Int]()
  val subtractions = createHandler[Int]()

  val state = subtractions.merge(additions).scan(0)((acc, cur) ⇒ acc + cur)

  val app1 = div(
    button(click(1) --> additions, "+"),
    button(click(-1) --> subtractions, "-"),
    span("Count: ", child <-- state)
  )

  def textFieldComponent(outputEvents: Sink[String]): VNode = {
    val textValues: Handler[String] = createStringHandler()

    val disabledValues: Observable[Boolean] =
      textValues.map(x ⇒ x.length < 5).startWith(true)

    val clearTexts: Handler[String] = createStringHandler("")
    div(
      label("Todo:"),
      input(inputString --> textValues, value <-- clearTexts),
      button(
        click(textValues) --> outputEvents,
        click("") --> clearTexts,
        disabled <-- disabledValues,
        "Submit",
      )
    )
  }

  def todoComponent(todo: String, deleteEvents: Sink[String]): VNode = {
    li(
      span(todo),
      button(
        click(todo) --> deleteEvents,
        "Delete"
      )
    )
  }

  def app2(): VNode = {
    val addEvents = createStringHandler()
    val deleteEvents = createStringHandler()

    def addToList(todo: String): List[String] ⇒ List[String] =
      (list: List[String]) ⇒ list :+ todo

    def removeFromList(todo: String) =
      (list: List[String]) ⇒ list.filter(_ != todo)

    val additions: Observable[List[String] ⇒ List[String]] =
      addEvents.map(addToList)
    val deletions: Observable[List[String] ⇒ List[String]] =
      deleteEvents.map(removeFromList)

    val state: Observable[List[String]] =
      additions.merge(deletions).scan(List.empty[String])((list, fn) ⇒ fn(list))

    val listView = state.map(_.map(todo ⇒ todoComponent(todo, deleteEvents)))

    div(
      textFieldComponent(addEvents),
      ul(children <-- listView)
    )
  }

  sealed trait Action
  case object Add extends Action
  case object Subtract extends Action

  type State = Int
  val initialState = 0

  def reducer(previousState: State, action: Action): State = action match {
    case Add ⇒ previousState + 1
    case Subtract ⇒ previousState - 1
  }

  private val storeSink: Handler[Action] = createHandler[Action]()
  private val intValues: Observable[State] =
    storeSink.scan(initialState)(reducer)

  val app3 = div(
    button(click(Add) --> storeSink, "+"),
    button(click(Subtract) --> storeSink, "-"),
    span("count: ", child <-- intValues)
  )

  val store = Store(0, reducer)
  val app4 = div(
    button(click(Add) --> store, "+"),
    button(click(Subtract) --> store, "-"),
    span("count: ", child <-- store)
  )

  sealed trait TodoActions {
    def reduce(previousState: List[String]) : List[String]
  }
  object TodoActions {
    case class AddItem(item: String) extends TodoActions {
      override def reduce(previousState: List[String]): List[String] = previousState :+ item
    }
    case class RemoveItem(item: String) extends TodoActions {
      override def reduce(previousState: List[String]): List[String] = previousState.filterNot(_ == item)
    }
  }

  def textFieldComponent2(outputEvents: Sink[TodoActions]): VNode = {
    val textValues: Handler[String] = createStringHandler()

    val disabledValues: Observable[Boolean] =
      textValues.map(x ⇒ x.length < 5).startWith(true)

    val clearTexts: Handler[String] = createStringHandler("")
    div(
      label("Todo:"),
      input(inputString --> textValues, value <-- clearTexts),
      button(
        click(textValues.map(AddItem)) --> outputEvents,
        click("") --> clearTexts,
        disabled <-- disabledValues,
        "Submit",
      )
    )
  }

  def todoComponent2(todo: String, deleteEvents: Sink[TodoActions]): VNode = {
    li(
      span(todo),
      button(
        click(RemoveItem(todo)) --> deleteEvents,
        "Delete"
      )
    )
  }

  private val reducer1: (List[String], TodoActions) ⇒ List[String] = (previousState: List[String], action: TodoActions) ⇒ action.reduce(previousState)
  private val store2 = Store(List.empty[String], reducer1)

  def app5(): VNode = {
    div(
      textFieldComponent2(store2.sink),
      ul(children <-- store2.source.map(listt ⇒ listt.map(todoComponent2(_, store2.sink))))
    )
  }
}
