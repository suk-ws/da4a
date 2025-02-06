package cc.sukazyo.std
package data.collections

trait InPlaceSortable [E] {
	
	def sortInPlace (using ordering: Ordering[E]): InPlaceSortable.this.type
	
}
