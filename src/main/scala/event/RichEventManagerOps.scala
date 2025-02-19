package cc.sukazyo.std
package event

/** todo: docs
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
trait RichEventManagerOps [EP, ER] extends AbstractRichEvent[EP, ER] {
	
	/** todo: docs
	  * @since 0.2.0
	  */
	type ContextInitializeOperator = EventContext[EP] => Unit
	
	/** todo: docs
	  * @since 0.2.0
	  */
	protected var contextInitializeOperation: Option[ContextInitializeOperator] = None
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @param callback
	  * @return
	  */
	def initContextWith (callback: ContextInitializeOperator): RichEventManagerOps.this.type = {
		if (contextInitializeOperation.isDefined)
			throw new RichEventManagerOps.ContextInitializerAlreadyDefinedException
		contextInitializeOperation = Some(callback)
		this
	}
	
	/** todo: docs
	  * @since 0.2.0
	  */
	type EventListenerErrorHandler = (Throwable, EventContext[EP]) => Boolean
	
	/** todo: docs
	  * @since 0.2.0
	  */
	protected var errorHandler: Option[EventListenerErrorHandler] = None
	
	/** todo: docs
	  * @since 0.2.0
	  * 
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
	def initErrorHandlerWith (callback: EventListenerErrorHandler): RichEventManagerOps.this.type = {
		if (errorHandler.isDefined)
			throw new RichEventManagerOps.ErrorHandlerAlreadyDefinedException
		errorHandler = Some(callback)
		this
	}
	
}

/** todo: docs
  * @since 0.2.0
  */
object RichEventManagerOps {
	
	/** todo: docs
	  * @since 0.2.0
	  * @param message
	  */
	class ConfigDuplicatedDefineException (message: String) extends RuntimeException(message)
	
	/** todo: docs
	  * @since 0.2.0
	  */
	class ContextInitializerAlreadyDefinedException
		extends Exception("Event context initializer has been defined, it cannot be re-defined.")
	
	class ErrorHandlerAlreadyDefinedException
		extends Exception("Event listeners' error handler has been defined, it cannot be re-defined.")
	
}
