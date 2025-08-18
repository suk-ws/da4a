package cc.sukazyo.std
package event

trait EventOps [EP, ER]
	extends AbstractEvent[EP, ER] {
	
	def --> (listener: this.MyCallback): EventOps.this.type =
		this.registerListener(listener)
	
}
