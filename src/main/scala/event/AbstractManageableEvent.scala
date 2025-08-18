package cc.sukazyo.std
package event

import event.AbstractManageableEvent.{ContextInitializerAlreadyDefinedException, ErrorHandlerAlreadyDefinedException}

trait AbstractManageableEvent [EP, ER] extends AbstractRichEvent[EP, ER] {
	
	/** todo: docs
	 * @since 0.2.0
	 */
	type ContextInitializeOperator = EventContext[EP] => Unit
	
	/** todo: docs
	 *
	 * @since 0.2.0
	 * @param callback
	 * @return
	 */
	@throws[ContextInitializerAlreadyDefinedException]
	def initContextWith (callback: ContextInitializeOperator): AbstractManageableEvent.this.type
	
	/** todo: docs
	 *
	 * @since 0.2.0
	 */
	type EventListenerErrorHandler = (Throwable, EventContext[EP]) => Boolean
	
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
	
}

object AbstractManageableEvent {
	
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
