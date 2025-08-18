package cc.sukazyo.std
package event

trait ManageableEvent [EP, ER] extends AbstractManageableEvent[EP, ER]
	with ManageableEventOps[EP, ER]
