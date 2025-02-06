package cc.sukazyo.std
package event

trait RichEventOps [EP, ER] 
	extends AbstractRichEvent[EP, ER] 
		with AbstractEvent[EP, ER] {
	
	override def registerListener (listener: MyCallback): RichEventOps.this.type = {
		this.registerListener(EventListener.simple[EP, ER](listener))
	}
	
	def registerListener (priority: Int)(listener: MyCallback): RichEventOps.this.type = {
		this.registerListener(EventListener.simple[EP, ER](listener, priority))
	}
	
	def registerRichListener (priority: Int)(listener: MyRichCallback): RichEventOps.this.type = {
		this.registerListener(EventListener(listener, priority))
	}
	
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
	
	def removeRichListener (listener: MyRichCallback): RichEventOps.this.type = {
		removeListenerIf {
			case richCbListener: EventListener.CallbackIsFunction[EP, ER] =>
				richCbListener.callbackFunction == listener
			case _ => false
		}
		this
	}
	
}
