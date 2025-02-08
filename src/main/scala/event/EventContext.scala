package cc.sukazyo.std
package event

import contexts.GivenContext
import datetime.DateTimeTypeAliases.EpochMillis
import stacks.WithCurrentStack

import scala.collection.mutable

/** Context of current emitted event.
  *
  * Once an event is emitted, a new [[EventContext]] will be generated, and it will be passed
  * to
  *
  * todo: docs
  * @since 0.2.0
  * 
  * @tparam EP
  */
trait EventContext [EP] {
	
	/** todo: docs
	  * @since 0.2.0
	  */
	val params: EP
	
	/** todo: docs
	  * @since 0.2.0
	  */
	private val _status: mutable.ListBuffer[EventStatus] = mutable.ListBuffer.empty
	
	/** [[GivenContext Given Contexts]] associated to the event. Can be used to store and share
	  * data between event listeners.
	  *
	  * todo: docs refactor
	  * @since 0.2.0
	  */
	val givenCxt: GivenContext = GivenContext()
	
	/** The [[EpochMillis]] time that bot received this event and preparing to process it.
	  *
	  * todo: docs refactor
	  * @since 0.2.0
	  */
	val timeStartup: EpochMillis = System.currentTimeMillis
	
	/** If this event is processed.
	  *
	  * Not only [[EventStatus.OK]] but also [[EventStatus.CANCELED]] will be seen as processed.
	  *
	  * todo: docs refactor
	  * @since 0.2.0
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
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @param state
	  */
	def pushEventState (state: EventStatus): Unit =
		_status += state
	
	/** Set the event status to [[EventStatus.OK]].
	  *
	  * This will push a new [[EventStatus.OK]] to the status list.
	  * 
	  * todo: docs refactor
	  * @since 0.2.0
	  */
	//noinspection UnitMethodIsParameterless
	def setEventOk: Unit =
		pushEventState(EventStatus.Success(WithCurrentStack.getStackHeadBeforeClass[EventContext[EP]]))
	
	/** Set the event status to [[EventStatus.CANCELED]].
	  *
	  * This will push a new [[EventStatus.CANCELED]] to the status list.
	  * 
	  * todo: docs refactor
	  * @since 0.2.0
	  */
	//noinspection UnitMethodIsParameterless
	def setEventCanceled: Unit =
		pushEventState(EventStatus.Cancelled(WithCurrentStack.getStackHeadBeforeClass[EventContext[EP]]))
	
	/** Get the last [[State]] set of the event.
	  *
	  * todo: docs refactor
	  * @since 0.2.0
	  */
	def state: EventStatus | Null =
		stateOption match
			case Some(x) => x
			case None => null
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @return
	  */
	def stateOption: Option[EventStatus] =
		_status.lastOption
	
	/** Get all the status set of the event. The earlier status set is on the left.
	  *
	  * todo: docs refactor
	  * @since 0.2.0
	  */
	def status: List[EventStatus] =
		_status.toList
	
}

/** todo: docs
  * @since 0.2.0
  */
object EventContext {
	
	/** todo: docs
	  * @since 0.2.0
	  * 
	  * @param _params
	  * @tparam EP
	  * @return
	  */
	def apply [EP] (_params: EP): EventContext[EP] = new EventContext[EP] {
		override val params: EP = _params
	}
	
}
