package cc.sukazyo.std
package event

trait SimpleEventOps [EP, ER]
	extends AbstractEvent[EP, ER] {
	
	def --> (listener: this.MyCallback): SimpleEventOps.this.type =
		this.registerListener(listener)
	
}
