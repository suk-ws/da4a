package cc.sukazyo.std

import event.impl.NormalEventManager

package object event {
	
	type Event[EP, ER] = RichEvent[EP, ER] & ManageableEvent[EP, ER]
	type SupplierEvent [EP] = Event[EP, Unit]
	type ConsumerEvent [ER] = Event[Unit, ER]
	
//	type SimpleEvent [EP, ER] = SimpleEvent[EP, ER]
	type SimpleSupplierEvent [EP] = SimpleEvent[EP, Unit]
	type SimpleConsumerEvent [ER] = SimpleEvent[Unit, ER]
	
	object Event {
		
		def apply[EP, ER] (): Event[EP, ER] =
			NormalEventManager[EP, ER]()
		
	}
	
}
