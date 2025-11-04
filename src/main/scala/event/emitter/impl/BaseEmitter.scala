package cc.sukazyo.std
package event.emitter.impl

import event.emitter.Emitter
import event.emitter.Emitter.UnsupportedEventTypeException
import event.*

import scala.collection.mutable.ListBuffer
import scala.util.boundary
import scala.util.boundary.break

trait BaseEmitter [EP, ER, EV <: AbstractEvent[EP, ER]] (event: EV) extends Emitter[EP, ER, EV] {
	
	override def emit (params: EP): List[ER] throws UnsupportedEventTypeException =
		event match
			case simpleEmittable: SimpleEmittableEvent[EP, ER] =>
				
				simpleEmittable.foreachListeners { listener =>
					listener.apply(params)
				}
				
			case richEmittableEvent: RichEmittableEvent[EP, ER] =>
				
				val context = EventContext(params)
				var errorHandler: Option[AbstractManageableEvent.EventListenerErrorHandler[EP]] = None
				richEmittableEvent match
					case manageableEvent: AbstractManageableEvent[EP, ER] =>
						this.patchContext(manageableEvent, context)
						errorHandler = this.getErrorHandler(manageableEvent)
				
				val returns: ListBuffer[ER] = ListBuffer.empty
				boundary { richEmittableEvent.foreachListeners { listener =>
					try
						if (!listener.isSkipEvent(using context))
							val cr = listener.callback(context)
							returns += cr
					catch case e: Throwable =>
						if (errorHandler.isDefined)
							if (!errorHandler.get.apply(e, context))
								break()
							else throw e
				}}
				returns.toList
				
			case _ => throw UnsupportedEventTypeException("Event must implements SimpleEmittableEvent or RichEmittableEvent to be emittable")
	
}
