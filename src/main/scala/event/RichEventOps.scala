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
trait RichEventOps [EP, ER] 
	extends AbstractRichEvent[EP, ER] 
		with AbstractEvent[EP, ER]
		with EventOps[EP, ER] {
	
	override def registerListener (listener: MyCallback): RichEventOps.this.type = {
		this.registerListener(EventListener.simple[EP, ER](listener))
	}
	
	def --> (listener: MyListener): RichEventOps.this.type = {
		this.registerListener(listener)
	}
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @param priority
	  * @param listener
	  * @return
	  */
	def registerListener (priority: Int)(listener: MyCallback): RichEventOps.this.type = {
		this.registerListener(EventListener.simple[EP, ER](listener, priority))
	}
	
	/** todo: docs
	 *
	 * @since 0.2.0
	 * @param priority
	 * @param listener
	 * @return
	 */
	def registerRichListener (listener: MyRichCallback): RichEventOps.this.type = {
		this.registerListener(EventListener(listener))
	}
	
	def ==> (listener: MyRichCallback): RichEventOps.this.type = {
		this.registerRichListener(listener)
	}
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @param priority
	  * @param listener
	  * @return
	  */
	def registerRichListener (priority: Int)(listener: MyRichCallback): RichEventOps.this.type = {
		this.registerListener(EventListener(listener, priority))
	}
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @param predicate
	  * @return
	  */
	def removeListenerIf (predicate: MyListener => Boolean): RichEventOps.this.type
	
	override def removeListener (listener: MyCallback): RichEventOps.this.type = {
		removeListenerIf {
			case simpleCbListener: EventListener.CallbackIsSimpleFunction[EP, ER] =>
				simpleCbListener.simpleCallbackFunction == listener
			case _ => false
		}
		this
	}
	
	override def removeListener (listener: MyListener): RichEventOps.this.type = {
		removeListenerIf(_ == listener)
		this
	}
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @param listener
	  * @return
	  */
	def removeRichListener (listener: MyRichCallback): RichEventOps.this.type = {
		removeListenerIf {
			case richCbListener: EventListener.CallbackIsFunction[EP, ER] =>
				richCbListener.callbackFunction == listener
			case _ => false
		}
		this
	}
	
}
