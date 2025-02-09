package cc.sukazyo.std
package event

/** The most basic event.
  *
  * Can only register and remove listeners and emit events.
  *
  * Event can have parameters and expected return values.
  * This will affect the listeners' received parameters and return values.
  *
  * The listener should be registered as a function instance.
  * When removing one listener, pass the function instance of the listener to the
  * [[removeListener]] method.
  * Due to you can only remove the listener by the function instance, you should keep the
  * function instance by yourself.
  *
  * How the listeners are ordered is determined by the implementations.
  * 
  * @since 0.2.0
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
trait AbstractEvent [EP, ER] {
	
	/** Type of the most basic listener in this event.
	  * 
	  * Receives the event parameters [[EP]] and return the event result [[ER]].
	  * 
	  * This types listener can be registered to the event using [[registerListener]] method.
	  * 
	  * @since 0.2.0
	  */
	type MyCallback = EP => ER
	
	/** Register a listener to the event.
	  *
	  * The listener should be a function instance that receives the event parameters [[EP]],
	  * and returns the event result [[ER]].
	  *
	  * If you want to remove the listener later, you should keep the function instance by
	  * yourself.
	  * 
	  * @since 0.2.0
	  *
	  * @param listener The listener function instance.
	  * @return The event instance itself, for the convenience of chained method calls.
	  */
	def registerListener (listener: MyCallback): AbstractEvent.this.type
	
	/** Remove a listener from the event.
	  *
	  * Remove the listener by the function instance.
	  *
	  * If the listener is not registered, do nothing.
	  * 
	  * @since 0.2.0
	  *
	  * @param listener The listener function instance.
	  * @return The event instance itself, for the convenience of chained method calls.
	  */
	def removeListener (listener: MyCallback): AbstractEvent.this.type
	
	/** Emit the event.
	  *
	  * This will call all the listeners with the provided event parameters [[EP]] in some
	  * order synchronized.
	  *
	  * The implementations determine the order of calling the listeners.
	  *
	  * The emit method doesn't catch errors.
	  * If any listener throws an unhandled exception, the exception will be thrown to the
	  * [[emit]] caller.
	  * 
	  * @since 0.2.0
	  *
	  * @param eventParams The event parameters [[EP]]. Will be passed to all the listeners.
	  * @return A list of the event results [[ER]]. Contains every listener's return value.
	  */
	def emit (eventParams: EP): List[ER]
	
}
