package cc.sukazyo.std
package data.collections

trait IteratingOps [E] {
	
	def removeIf (predicate: E => Boolean): IteratingOps.this.type
	
}
