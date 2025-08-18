package cc.sukazyo.std
package event.impl

import event.SimpleEvent

import scala.collection.mutable

/**
  * An event that can be listened to.
  *
  * @tparam T The event parameters. Should be provided when triggering this event, and will be
  *           passed to the listeners.
  * @tparam R The return type that can/should be returned by the event listener.
  */
class SimpleEventManager [T, R] extends SimpleEvent[T, R] {
	
	private val _listeners: mutable.Map[Object, MyCallback] = mutable.HashMap.empty
	
	// todo docs
	override def registerListener (listener: MyCallback): SimpleEventManager.this.type =
		this.addListener(listener)
		this
	
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
	def addListener (key: Object)(func: MyCallback): Unit =
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
	def addListener (func: MyCallback): Unit =
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
	def += (it: (Object, MyCallback)): Unit =
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
	def += (func: MyCallback): Unit =
		this.addListener(func)
	
	// todo docs
	override def removeListener (listener: MyCallback): SimpleEventManager.this.type =
		this.removeListener(listener)
		this
	
	/**
	  * Remove a listener by the key of the listener.
	  *
	  * @param obj the key of the listener that will be removed.
	  * @return [[Some]] listener function that have be successfully removed, or [[None]] if no
	  *         listener is removed.
	  *
	  * @since 0.2.0
	  */
	def removeListener (obj: Object): Option[MyCallback] =
		_listeners.remove(obj)
	
	/**
	  * A map, contains all the registered listeners.
	  *
	  * The map's key is a listener's key, and the value is the listener function.
	  */
	def listeners: Map[Object, MyCallback] =
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
	
	// todo docs
	override def emit (eventParams: T): List[R] =
		this.apply(eventParams)
	
	private def invoking (params: T)(listener: MyCallback): R =
		listener.apply(params)
	
}

object SimpleEventManager {
	
	def apply [T, R]: SimpleEventManager[T, R] =
		new SimpleEventManager[T, R] {}
	
	def simple [T]: SimpleEventManager[T, Unit] =
		new SimpleEventManager[T, Unit] {}
	
	def basic: SimpleEventManager[Unit, Unit] =
		new SimpleEventManager[Unit, Unit] {}
	
}
