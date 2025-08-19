package cc.sukazyo.std
package event

import event.impl.SimpleEventManager

trait SimpleEvent[EP, ER]
	extends AbstractEvent[EP, ER]
		with SimpleEventOps[EP, ER]
		with SimpleEmittableEvent[EP, ER]

object SimpleEvent {
	
	def apply[EP, ER](): SimpleEvent[EP, ER] =
		SimpleEventManager.apply
	
}
