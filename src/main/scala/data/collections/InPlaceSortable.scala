package cc.sukazyo.std
package data.collections

/** Base trait for in-place sortable collections.
  * 
  * The in-place sorting means that the collection will be sorted in place.
  * The original collection will be modified, and doesn't produce a copy of the collection.
  * Compared to the normal sorting, the in-place sorting should be more efficient in terms of
  * memory usage, giving better performances, especially in a list that needs inserts and sorts
  * many times.
  * 
  * Due to the nature of in-place sorting, the collection should be mutable, and the sorting
  * algorithm should be able to modify the collection directly.
  * 
  * @since 0.2.0
  * 
  * @tparam E Type of the elements in the collection, also the type of the elements that will
  *           be sorted.
  */
trait InPlaceSortable [E] {
	
	/** Sort the collection in-place.
	  * 
	  * The collection will be sorted in place, without producing a new collection.
	  * So that this method will modify the original collection, producing side effects.
	  * 
	  * This method returns the original collection itself, so that it can be used in chained
	  * method calls.
	  * 
	  * @since 0.2.0
	  * 
	  * @param ordering The ordering to be used to sort the collection.
	  * @return The collection itself.
	  */
	def sortInPlace (using ordering: Ordering[E]): InPlaceSortable.this.type
	
}
