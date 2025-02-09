package cc.sukazyo.std
package event

/** Event that has rich-featured listeners supports.
  * 
  * @since 0.2.0
  * 
  * @tparam EP The type of the event parameters. Parameters should be provided when emitting
  *            the event, and all the listeners will receive the parameters.
  *
  *            For most cases that you don't need to provide parameters, the type can be
  *            [[Unit]].
  *
  * @tparam ER The type of the event result.
  *            Every listener should return a value in this type,
  *            and it will be collected and returned as a list to the event emitter.
  *
  *            For most cases that you don't need listeners to return something, the return
  *            type can be [[Unit]].
  */
trait AbstractRichEvent [EP, ER] extends AbstractEvent[EP, ER] {
	
	/** Type of the rich listener object in this event.
	  *
	  * A [[EventListener]] with the same [[EP]] and [[ER]] type params.
	  *
	  * This type of listener should be able to be registered to this event using
	  * [[registerListener(MyListener)]] method.
	  *
	  * @since 0.2.0
	  */
	type MyListener = EventListener[EP, ER]
	
	/** Type of the rich listener's callback in this event.
	  * 
	  * A [[EventListener.RichCallback]] with the same [[EP]] and [[ER]] type params.
	  * 
	  * In the [[RichEventOps]] trait, there are some methods can register this type of callback.
	  * 
	  * @since 0.2.0
	  */
	type MyRichCallback = EventListener.RichCallback[EP, ER]
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @param listener
	  * @return
	  */
	def registerListener (listener: MyListener): AbstractRichEvent.this.type
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @param listener
	  * @return
	  */
	def removeListener (listener: MyListener): AbstractRichEvent.this.type
	
}
