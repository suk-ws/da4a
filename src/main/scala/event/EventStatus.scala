package cc.sukazyo.std
package event

/** todo: docs
  * @since 0.2.0
  */
trait EventStatus

/** Available status of the event.
  * @since 0.2.0
  */
object EventStatus {
	
	/** This status contains a [[StackTraceElement]] that shows where the status is set.
	  * @since 0.2.0
	  */
	trait WithSource {
		/** todo: docs
		  * @since 0.2.0
		  */
		val from: StackTraceElement
	}
	
	/** The event is successfully processed by someone event listener.
	  * @since 0.2.0
	  */
	case class Success (override val from: StackTraceElement) extends EventStatus with WithSource
	/** The event is canceled for some reason, it is recommended be ignored.
	  * @since 0.2.0
	  */
	case class Cancelled (override val from: StackTraceElement) extends EventStatus with WithSource
	
	/** todo: docs
	  * @since 0.2.0
	  */
	case class Custom () extends EventStatus
	
}
