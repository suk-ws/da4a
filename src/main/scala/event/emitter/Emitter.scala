package cc.sukazyo.std
package event.emitter

import event.emitter.impl.BaseEmitter
import event.{AbstractEvent, AbstractManageableEvent, EventContext}

trait Emitter [EP, ER, EV <: AbstractEvent[EP, ER]] {
	
	def emit (params: EP): List[ER]
	
	protected def patchContext (event: AbstractManageableEvent[EP, ER], context: EventContext[EP]): Unit =
		event.patchContext(context)
	
	protected def getErrorHandler (event: AbstractManageableEvent[EP, ER]): Option[event.EventListenerErrorHandler] =
		event.getErrorHandler
	
}

object Emitter {
	
	class UnsupportedEventTypeException (message: String) extends Exception(message)
	
	def emittingFor[EP, ER, EV <: AbstractEvent[EP, ER]] (event: EV): Emitter[EP, ER, EV] =
		new BaseEmitter(event) {}
	
}
