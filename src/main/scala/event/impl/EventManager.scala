package cc.sukazyo.std
package event.impl

import data.collections.LinkedList
import event.{AbstractEvent, AbstractRichEvent, EventContext, EventListener, RichEventOps}

import scala.collection.mutable

trait EventManager [EP, ER]
	extends AbstractEvent[EP, ER]
		with AbstractRichEvent[EP, ER]
		with RichEventOps[EP, ER] {
	
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
		listeners.foreach { listener =>
			if (!listener.isSkipEvent)
				val cr = listener.callback(context)
				returns += cr
		}
		returns.toList
	
}
