package cc.sukazyo.std
package event

import event.AbstractManageableEvent.{ContextInitializerAlreadyDefinedException, ErrorHandlerAlreadyDefinedException}

trait AbstractManageableEvent [EP, ER] extends AbstractRichEvent[EP, ER] {
	
	/** todo: docs
	 * @since 0.2.0
	 */
	type ContextInitializeOperator = AbstractManageableEvent.ContextInitializer[EP]
	
	/** todo: docs
	 *
	 * @since 0.2.0
	 * @param callback
	 * @return
	 */
	@throws[ContextInitializerAlreadyDefinedException]
	def initContextWith (callback: ContextInitializeOperator): AbstractManageableEvent.this.type
	
	protected[event] def patchContext (context: EventContext[EP]): Unit
	
	/** todo: docs
	 *
	 * @since 0.2.0
	 */
	type EventListenerErrorHandler = AbstractManageableEvent.EventListenerErrorHandler[EP]
	
	/** todo: docs
	 *
	 * @since 0.2.0
	 * @param callback
	 * @return A boolean value.
	 *         When it is `true`, then the event manager will continue to
	 *         call the next listeners as normal.
	 *
	 *         When it is `false`, the event manager will immediately stop emitting current
	 *         event to the rest of the listeners.
	 *         This behavior skips the [[EventContext.isEventOk]] checks, so even the
	 *         [[EventListener.isSkipEvent]] will not work in this case.
	 *         It also doesn't create a new [[EventStatus]] for [[EventContext]].
	 */
	@throws[ErrorHandlerAlreadyDefinedException]
	def initErrorHandlerWith (callback: EventListenerErrorHandler): AbstractManageableEvent.this.type
	
	protected[event] def getErrorHandler: Option[EventListenerErrorHandler]
	
	/** Emit the event.
	 *
	 * This will call all the listeners with the provided event parameters [[EP]] in some
	 * order synchronized.
	 *
	 * The implementations determine the order of calling the listeners.
	 *
	 * When the event implements [[AbstractManageableEvent]], and the [[initErrorHandlerWith]]
	 * is set, the emit method will catch errors and pass the errors into the error handler
	 * that [[initErrorHandlerWith]] defines. Based on the return value of error handler, the
	 * emitting will continue or be stopped.
	 *
	 * If no error handler defined by [[initErrorHandlerWith]], the error will be thrown to the
	 * [[emit]] caller, like normal event's [[AbstractEvent.emit]] method.
	 *
	 * @since 0.2.0
	 * @deprecated use [[foreachListeners]] and [[cc.sukazyo.std.event.emitter.Emitter]] instead.
	 * @param eventParams The event parameters [[EP]]. Will be passed to all the listeners.
	 * @return A list of the event results [[ER]]. Contains every listener's return value.
	 *         If one listener throws an error, the result of that listener will be missing.
	 */
	override def emit (eventParams: EP): List[ER]
	
}

object AbstractManageableEvent {
	
	type ContextInitializer [EP] = EventContext[EP] => Unit
	type EventListenerErrorHandler [EP] = (Throwable, EventContext[EP]) => Boolean
	
	/** todo: docs
	 *
	 * @since 0.2.0
	 * @param message
	 */
	class ConfigDuplicatedDefineException (message: String) extends RuntimeException(message)
	
	/** todo: docs
	 *
	 * @since 0.2.0
	 */
	class ContextInitializerAlreadyDefinedException
		extends Exception("Event context initializer has been defined, it cannot be re-defined.")
	
	class ErrorHandlerAlreadyDefinedException
		extends Exception("Event listeners' error handler has been defined, it cannot be re-defined.")
	
}
