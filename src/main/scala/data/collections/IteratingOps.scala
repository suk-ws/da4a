package cc.sukazyo.std
package data.collections

/** Base trait of some iterating related operations.
  * 
  * @since 0.2.0
  * 
  * @tparam E The type of elements in the collection.
  */
trait IteratingOps [E] {
	
	/** Remove all elements that satisfy the predicate.
	  * 
	  * This method iterates through the collection and removes all elements that satisfy the
	  * predicate function in-place.
	  * 
	  * Notice that this method removes elements in-place, which means that the original
	  * collection will be modified, and shouldn't generate a new collection.
	  * Due to no new collection created, this method returns the original collection itself
	  * for method chaining.
	  * 
	  * @example
	  * {{{
	  *     // normally you can use LinkedList
	  *     val list = SomeIteratingList(1, 2, 3, 4, 5)
	  *     list.removeIf(_ % 2 == 0)
	  *     println(list)
	  *     // list is now SomeIteratingList(1, 3, 5)
	  * }}}
	  * 
	  * @since 0.2.0
	  * 
	  * @param predicate The predicate function to determine whether an element should be
	  *                  removed.
	  *                  
	  *                  It receives an element and should return a boolean value.
	  *                  If the return value is `true`, the element will be removed, otherwise
	  *                  the element will be kept.
	  *                  
	  * @return The original collection itself.
	  */
	def removeIf (predicate: E => Boolean): IteratingOps.this.type
	
}
