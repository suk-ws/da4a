package cc.sukazyo.std
package event

trait SimpleEvent[EP, ER]
	extends AbstractEvent[EP, ER]
		with SimpleEventOps[EP, ER]
