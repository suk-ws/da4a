package cc.sukazyo.std
package event.impl

import data.collections.LinkedList
import event.{AbstractEvent, AbstractRichEvent, EventContext, EventListener, RichEvent, RichEventManagerOps, RichEventOps}

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

/** todo: docs
  *
  * @tparam EP The type of the event parameters. Parameters should be provided when emitting
  *            the event, and all the listeners will receive the parameters.
  *
  *            For most cases that you don't need to provide parameters, the type can be
  *            [[Unit]].
  *
  * @tparam ER The type of the event result.
  *            Every listener should return a value in this type,
  *            and it will be collected and returned as a list to the event emitter.
  *
  *            For most cases that you don't need listeners to return something, the return
  *            type can be [[Unit]].
  */
trait EventManager [EP, ER]
	extends RichEvent[EP, ER]
		with AbstractEvent[EP, ER]
		with AbstractRichEvent[EP, ER]
		with RichEventOps[EP, ER]
		with RichEventManagerOps[EP, ER] {
	
	type MyListener = EventListener[EP, ER]
	
	protected val listeners: LinkedList[MyListener] =
		LinkedList.empty[MyListener]
	
	override def registerListener (listener: MyListener): EventManager.this.type = {
		listeners.addOne(listener)
		this
	}
	
	override def removeListenerIf (predicate: MyListener => Boolean): EventManager.this.type = {
		listeners.removeIf(predicate)
		this
	}
	
	override def emit (eventParams: EP): List[ER] =
		val returns = mutable.ListBuffer.empty[ER]
		implicit val context: EventContext[EP] = EventContext(eventParams)
		// initialize the context. provided by [[RichEventManagerOps]]
		if (this.contextInitializeOperation.isDefined)
			this.contextInitializeOperation.get.apply(context)
		// call all the listeners in order.
		boundary { listeners.foreach { listener =>
			try {
				if (!listener.isSkipEvent)
					val cr = listener.callback(context)
					returns += cr
			} catch case e: Throwable => {
				// if the error handler is defined, then call error handler
				//  and on that returns 'false', end the loop.
				// if no error handler, just throw it out.
				if (this.errorHandler.isDefined)
					if (!this.errorHandler.get.apply(e, context))
						break()
				else throw e
			}
		}}
		returns.toList
	
}
