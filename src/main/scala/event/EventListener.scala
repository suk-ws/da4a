package cc.sukazyo.std
package event

/** A rich-featured event listener.
  *
  * Comparing to the simple event listener (which is just a function), it has a priority that
  * can help event manager to determine the order of listeners to be called; it also has a
  * method that can be used to determine whether the event should be skipped according to the
  * current event's [[EventContext]].
  *
  * @since 0.2.0
  *
  * @tparam EP The event parameters' type.
  *            And also the parameters' type that listener's callback method will receive.
  *
  * @tparam ER The event result's type.
  *            And also the return type of the listener's callback method.
  */
trait EventListener [-EP, +ER] {
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @param context
	  * @return
	  */
	def callback(context: EventContext[EP]): ER
	
	/** todo: docs
	  * @since 0.2.0
	  */
	val priority: Int
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @param context
	  * @return
	  */
	def isSkipEvent (using context: EventContext[EP]): Boolean =
		context.isEventOk
	
}

/** todo: docs
  * @since 0.2.0
  */
object EventListener {
	
	/**
	 * Provides an [[Ordering]] for [[EventListener]] instances based on their priority.
	 *
	 * @since 0.2.0
	 *
	 * @tparam EP The event parameter type.
	 * @tparam ER The event result type.
	 * @return An [[Ordering]] instance that compares [[EventListener]] instances first by their priority,
	 *         and if the priorities are equal, by their hash codes.
	 * 
	 * @example {{{
	 *     val listener1 = new EventListener[String, Unit] {
	 *         val callback: String => Unit = _ => println("Listener 1")
	 *         val priority: Int = 10
	 *     }
	 *     val listener2 = new EventListener[String, Unit] {
	 *         val callback: String => Unit = _ => println("Listener 2")
	 *         val priority: Int = 5
	 *     }
	 *     val listeners = List(listener1, listener2).sorted(OrderingByPriority)
	 *     // listeners: List(listener2, listener1)
	 * }}}
	 */
	def OrderingByPriorityFixed [EP, ER]: Ordering[EventListener[EP, ER]] = (x, y) =>
			if (x.priority == y.priority) x.hashCode `compareTo` y.hashCode
			else x.priority `compareTo` y.priority
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @tparam EP
	  * @tparam ER
	  * @return
	  */
	def OrderingByPriorityStables [EP, ER]: Ordering[EventListener[EP, ER]] = (x, y) =>
		x.priority `compareTo` y.priority
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @tparam EP
	  * @tparam ER
	  */
	type SimpleCallback [EP, ER] = EP => ER
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @tparam EP
	  * @tparam ER
	  */
	type RichCallback [EP, ER] = EventContext[EP] => ER
	
	/** todo: docs
	  * @since 0.2.0
	  */
	object Priorities {
		
		/** todo: docs
		  * @since 0.2.0
		  */
		val ABSOLUTE_HIGH: Int = Int.MaxValue
		/** todo: docs
		  * @since 0.2.0
		  */
		val HIGHEST      : Int = Short.MaxValue
		/** todo: docs
		  * @since 0.2.0
		  */
		val HIGHER       : Int = Byte.MaxValue
		/** todo: docs
		  * @since 0.2.0
		  */
		val HIGH         : Int = 64
		/** todo: docs
		  * @since 0.2.0
		  */
		val NORMAL       : Int = 0
		/** todo: docs
		  * @since 0.2.0
		  */
		val LOW          : Int = -64
		/** todo: docs
		  * @since 0.2.0
		  */
		val LOWER        : Int = Byte.MinValue
		/** todo: docs
		  * @since 0.2.0
		  */
		val LOWEST       : Int = Short.MinValue
		/** todo: docs
		  * @since 0.2.0
		  */
		val ABSOLUTE_LOW : Int = Int.MinValue
		
	}
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @tparam EP The event parameters' type.
	  *            And also the parameters' type that listener's callback method will receive.
	  *
	  * @tparam ER The event result's type.
	  *            And also the return type of the listener's callback method.
	  */
	trait CallbackIsFunction [EP, ER] extends EventListener[EP, ER] {
		val callbackFunction: RichCallback[EP, ER]
		override def callback (context: EventContext[EP]): ER = callbackFunction(context)
	}
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @tparam EP The event parameters' type.
	  *            And also the parameters' type that listener's callback method will receive.
	  *
	  * @tparam ER The event result's type.
	  *            And also the return type of the listener's callback method.
	  */
	trait CallbackIsSimpleFunction [EP, ER] extends CallbackIsFunction[EP, ER] {
		val simpleCallbackFunction: SimpleCallback[EP, ER]
		override val callbackFunction: RichCallback[EP, ER] = context => simpleCallbackFunction(context.params)
	}
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @param _callback
	  * @param _priority
	  * @tparam EP
	  * @tparam ER
	  * @return
	  */
	def apply [EP, ER] (_callback: EventContext[EP] => ER, _priority: Int): EventListener[EP, ER] =
		new CallbackIsFunction[EP, ER] {
			override val callbackFunction: RichCallback[EP, ER] = _callback
			override val priority: Int = _priority
		}
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @param _callback
	  * @tparam EP
	  * @tparam ER
	  * @return
	  */
	def apply [EP, ER] (_callback: EventContext[EP] => ER): EventListener[EP, ER] =
		apply(_callback, Priorities.NORMAL)
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @param _callback
	  * @param _priority
	  * @tparam EP
	  * @tparam ER
	  * @return
	  */
	def simple [EP, ER] (_callback: EP => ER, _priority: Int): EventListener[EP, ER] =
		new CallbackIsSimpleFunction[EP, ER] {
			override val simpleCallbackFunction: SimpleCallback[EP, ER] = _callback
			override val priority: Int = _priority
		}
	
	/** todo: docs
	  * @since 0.2.0
	  *
	  * @param _callback
	  * @tparam EP
	  * @tparam ER
	  * @return
	  */
	def simple [EP, ER] (_callback: EP => ER): EventListener[EP, ER] =
		simple(_callback, Priorities.NORMAL)
	
}
