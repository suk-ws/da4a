package cc.sukazyo.std
package event

import event.AbstractManageableEvent.{ContextInitializerAlreadyDefinedException, ErrorHandlerAlreadyDefinedException}

/** todo: docs
 *
 * @since 0.2.0
 * @tparam EP The type of the event parameters. Parameters should be provided when emitting
  *            the event, and all the listeners will receive the parameters.
  *
  *            For most cases that you don't need to provide parameters, the type can be
  *            [[Unit]].
 * @tparam ER The type of the event result.
  *            Every listener should return a value in this type,
  *            and it will be collected and returned as a list to the event emitter.
  *
  *            For most cases that you don't need listeners to return something, the return
  *            type can be [[Unit]].
  */
trait ManageableEventOps [EP, ER] extends AbstractManageableEvent[EP, ER] {
	
	/** todo: docs
	  * @since 0.2.0
	  */
	private var contextInitializeOperation: Option[ContextInitializeOperator] = None
	
	@throws[ContextInitializerAlreadyDefinedException]
	override def initContextWith (callback: ContextInitializeOperator): ManageableEventOps.this.type = {
		if (contextInitializeOperation.isDefined)
			throw new ContextInitializerAlreadyDefinedException
		contextInitializeOperation = Some(callback)
		this
	}
	
	override protected[event] def patchContext (context: EventContext[EP]): Unit =
		this.contextInitializeOperation match
			case Some(contextInitializer) =>
				contextInitializer(context)
			case None =>
	
	/** todo: docs
	  * @since 0.2.0
	  */
	private var errorHandler: Option[EventListenerErrorHandler] = None
	
	@throws[ErrorHandlerAlreadyDefinedException]
	override def initErrorHandlerWith (callback: EventListenerErrorHandler): ManageableEventOps.this.type = {
		if (errorHandler.isDefined)
			throw new ErrorHandlerAlreadyDefinedException
		errorHandler = Some(callback)
		this
	}
	
	override protected[event] def getErrorHandler =
		errorHandler
	
}
