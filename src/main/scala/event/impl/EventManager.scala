package cc.sukazyo.std
package event.impl

import data.collections.LinkedList
import event.emitter.Emitter
import event.{EventListener, ManageableEvent, RichEvent}

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
trait EventManager [EP, ER] extends RichEvent[EP, ER]
	with ManageableEvent[EP, ER] {
	
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
	
	override def foreachListeners[T] (cb: MyListener => T): List[T] = {
		listeners.toList
			.map(x => cb(x))
	}
	
	override def emit (eventParams: EP): List[ER] =
		Emitter.emittingFor(this).emit(eventParams)
	
}
