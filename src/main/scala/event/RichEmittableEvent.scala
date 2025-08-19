package cc.sukazyo.std
package event

trait RichEmittableEvent [EP, ER]
	extends AbstractRichEvent [EP, ER] {
	
	/** Iterate all the registered listeners.
	 *
	 * The order of the listener depends on the Event managers implementation. It might not
	 * be stable, or sorted by order added.
	 *
	 * @param cb A function that receives one listener. It can also return a value,
	 *           which will be stored to a List and returned, like a `map` function
	 *           does.
	 * @return For each callback function, the list of the returned value.
	 */
	def foreachListeners[T] (cb: MyListener => T): List[T]
	
}
