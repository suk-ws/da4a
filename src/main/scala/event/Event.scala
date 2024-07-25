package cc.sukazyo.std
package event

import scala.collection.mutable

/**
  * An event that can be listened to.
  *
  * @tparam T The event parameters. Should be provided when triggering this event, and will be
  *           passed to the listeners.
  * @tparam R The return type that can/should be returned by the event listener.
  */
trait Event [T, R] {
	
	type X = Function[T, R]
	
	private val _listeners: mutable.Map[Object, X] = mutable.HashMap.empty
	
	/**
	  * Setup a listener to this event. The listener will be called when the event is triggered.
	  *
	  * @param key The key of this listener.
	  *
	  *            If another listener with the same key is already added, then that one will
	  *            be automatically removed when this new listener is added.
	  *
	  *            You can also remove this listener later using <code>[[removeListener]](key)</code>
	  * @param func A function, will be called when the event is triggered. aka the listener of
	  *             this event.
	  *
	  *             Based on the differences usage, the event might be triggered multi times and
	  *             this listener will be called multi times also.
	  *
	  * @since 0.2.0
	  */
	def addListener (key: Object)(func: X): Unit =
		_listeners += (key, func)
	
	/**
	  * Setup a listener to this event.
	  *
	  * @param func A function that will be called when the event is triggered. aka the listener
	  *             of this event.
	  *
	  *             Also this function itself will be the key of this listener,  means you can
	  *             remove this listener later using <code>[[removeListener]](func)</code>.
	  *
	  * @see [[addListener(Object)(Func)]]
	  *
	  * @since 0.2.0
	  */
	def addListener (func: X): Unit =
		this.addListener(key = func)(func)
	
	/**
	  * Setup a listener to this event. The listener will be called when the event is triggered.
	  *
	  * @param key  The key of this listener.
	  *
	  *             If another listener with the same key is already added, then that one will
	  *             be automatically removed when this new listener is added.
	  *
	  *             You can also remove this listener later using <code>[[removeListener]](key)</code>
	  * @param func A function, will be called when the event is triggered. aka the listener of
	  *             this event.
	  *
	  *             Based on the differences usage, the event might be triggered multi times and
	  *             this listener will be called multi times also.
	  *
	  * @since 0.2.0
	  */
	def += (it: (Object, X)): Unit =
		this.addListener(it._1)(it._2)
	
	/**
	  * Setup a listener to this event.
	  *
	  * @param func A function that will be called when the event is triggered. aka the listener
	  *             of this event.
	  *
	  *             Also this function itself will be the key of this listener,  means you can
	  *             remove this listener later using <code>[[removeListener]](func)</code>.
	  *
	  * @see [[addListener(Object)(Func)]]
	  *
	  * @since 0.2.0
	  */
	def += (func: X): Unit =
		this.addListener(func)
	
	/**
	  * Remove a listener by the key of the listener.
	  *
	  * @param obj the key of the listener that will be removed.
	  * @return [[Some]] listener function that have be successfully removed, or [[None]] if no
	  *         listener is removed.
	  *
	  * @since 0.2.0
	  */
	def removeListener (obj: Object): Option[X] =
		_listeners.remove(obj)
	
	/**
	  * A map, contains all the registered listeners.
	  *
	  * the map's key is a listener's key, and the value is the listener function.
	  */
	def listeners: Map[Object, X] =
		_listeners.toMap
	
	/**
	  * Triggers this listener.
	  *
	  * @param params The parameters of this event, also will be passed to the listener.
	  * @return A list contains the process results of the listeners.
	  */
	def apply (params: T): List[R] =
		_listeners.values
			.toList
			.map(this.invoking(params)(_))
	
	private def invoking (params: T)(listener: X): R =
		listener.apply(params)
	
}

object Event {
	
	def apply [T, R]: Event[T, R] =
		new Event[T, R] {}
	
	def simple [T]: Event[T, Unit] =
		new Event[T, Unit] {}
	
	def basic: Event[Unit, Unit] =
		new Event[Unit, Unit] {}
	
}
