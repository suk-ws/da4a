package cc.sukazyo.std
package event

trait AbstractRichEvent [EP, ER] extends AbstractEvent[EP, ER] {
	
	type MyListener = EventListener[EP, ER]
	type MyRichCallback = EventListener.RichCallback[EP, ER]
	
	def registerListener (listener: MyListener): AbstractRichEvent.this.type
	
	def removeListener (listener: MyListener): AbstractRichEvent.this.type
	
}
