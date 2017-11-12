package org.csw.monitoringserverspike

import org.csw.monitoringserverspike.ScalaJSExample.TodoActions.{AddItem, RemoveItem}
import outwatch.Sink
import outwatch.dom._
import outwatch.util.Store
import rxscalajs.Observable

object ScalaJSExample extends App {

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

  OutWatch.render("#app", app5())
}
