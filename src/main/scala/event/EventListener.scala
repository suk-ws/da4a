package cc.sukazyo.std
package event

trait EventListener [EP, ER] {
	
	def callback(context: EventContext[EP]): ER
	val priority: Int
	
	def isSkipEvent (using context: EventContext[EP]): Boolean =
		context.isEventOk
	
}

object EventListener {
	
	/**
	 * Provides an ordering for [[EventListener]] instances based on their priority.
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
	
	def OrderingByPriorityStables [EP, ER]: Ordering[EventListener[EP, ER]] = (x, y) =>
		x.priority `compareTo` y.priority
	
	type SimpleCallback [EP, ER] = EP => ER
	type RichCallback [EP, ER] = EventContext[EP] => ER
	
	object Priorities {
		
		val ABSOLUTE_HIGH: Int = Int.MaxValue
		val HIGHEST      : Int = Short.MaxValue
		val HIGHER       : Int = Byte.MaxValue
		val HIGH         : Int = 64
		val NORMAL       : Int = 0
		val LOW          : Int = -64
		val LOWER        : Int = Byte.MinValue
		val LOWEST       : Int = Short.MinValue
		val ABSOLUTE_LOW : Int = Int.MinValue
		
	}
	
	trait CallbackIsFunction [EP, ER] extends EventListener[EP, ER] {
		val callbackFunction: RichCallback[EP, ER]
		override def callback (context: EventContext[EP]): ER = callbackFunction(context)
	}
	
	trait CallbackIsSimpleFunction [EP, ER] extends CallbackIsFunction[EP, ER] {
		val simpleCallbackFunction: SimpleCallback[EP, ER]
		override val callbackFunction: RichCallback[EP, ER] = context => simpleCallbackFunction(context.params)
	}
	
	def apply [EP, ER] (_callback: EventContext[EP] => ER, _priority: Int): EventListener[EP, ER] =
		new CallbackIsFunction[EP, ER] {
			override val callbackFunction: RichCallback[EP, ER] = _callback
			override val priority: Int = _priority
		}
	
	def apply [EP, ER] (_callback: EventContext[EP] => ER): EventListener[EP, ER] =
		apply(_callback, Priorities.NORMAL)
	
	def simple [EP, ER] (_callback: EP => ER, _priority: Int): EventListener[EP, ER] =
		new CallbackIsSimpleFunction[EP, ER] {
			override val simpleCallbackFunction: SimpleCallback[EP, ER] = _callback
			override val priority: Int = _priority
		}
	
	def simple [EP, ER] (_callback: EP => ER): EventListener[EP, ER] =
		simple(_callback, Priorities.NORMAL)
	
}
