package cc.sukazyo.std
package event

import contexts.GivenContext
import stacks.WithCurrentStack

import scala.collection.mutable

trait EventContext [EP] {
	
	val params: EP
	
	type EpochMillis = Long
	
	private val _status: mutable.ListBuffer[EventStatus] = mutable.ListBuffer.empty
	/** [[GivenContext Given Contexts]] associated to the event. Can be used to store and share
	  * data between event listeners.
	  *
	  * @since 1.3.0
	  */
	val givenCxt: GivenContext = GivenContext()
	/** The [[EpochMillis]] time that bot received this event and preparing to process it.
	  *
	  * @since 1.3.0
	  */
	val timeStartup: EpochMillis = System.currentTimeMillis
	
	/** If this event is processed.
	  *
	  * Not only [[EventStatus.OK]] but also [[EventStatus.CANCELED]] will be seen as processed.
	  *
	  * @since 1.2.0
	  *
	  * @return `true` if the event has been processed, `false` otherwise.
	  */
	def isEventOk: Boolean =
		stateOption match
			case Some(state) =>
				if (
					state.isInstanceOf[EventStatus.Success] ||
					state.isInstanceOf[EventStatus.Cancelled]
				) true
				else false
			case None => false
	
	// TODO: docs
	def pushEventState (state: EventStatus): Unit =
		_status += state
	
	/** Set the event status to [[EventStatus.OK]].
	  *
	  * This will push a new [[EventStatus.OK]] to the status list.
	  */
	//noinspection UnitMethodIsParameterless
	def setEventOk: Unit =
		pushEventState(EventStatus.Success(WithCurrentStack.getStackHeadBeforeClass[EventContext[EP]]))
	
	/** Set the event status to [[EventStatus.CANCELED]].
	  *
	  * This will push a new [[EventStatus.CANCELED]] to the status list.
	  *
	  * @since 1.3.0
	  */
	//noinspection UnitMethodIsParameterless
	def setEventCanceled: Unit =
		pushEventState(EventStatus.Cancelled(WithCurrentStack.getStackHeadBeforeClass[EventContext[EP]]))
	
	/** Get the last [[State]] set of the event.
	  *
	  * @since 1.3.0
	  */
	def state: EventStatus | Null =
		stateOption match
			case Some(x) => x
			case None => null
	
	// TODO: docs
	def stateOption: Option[EventStatus] =
		_status.lastOption
	
	/** Get all the status set of the event. The earlier status set is on the left.
	  *
	  * @since 1.3.0
	  */
	def status: List[EventStatus] =
		_status.toList
	
}

object EventContext {
	
	def apply [EP] (_params: EP): EventContext[EP] = new EventContext[EP] {
		override val params: EP = _params
	}
	
}
