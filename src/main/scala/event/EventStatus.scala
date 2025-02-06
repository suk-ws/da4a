package cc.sukazyo.std
package event

// TODO: docs
trait EventStatus

/** Available status of the event. */
object EventStatus {
	
	/** This status contains a [[StackTraceElement]] that shows where the status is set. */
	trait WithSource {
		val from: StackTraceElement
	}
	
	/** The event is successfully processed by someone event listener. */
	case class Success (override val from: StackTraceElement) extends EventStatus with WithSource
	/** The event is canceled for some reason, it is recommended be ignored. */
	case class Cancelled (override val from: StackTraceElement) extends EventStatus with WithSource
	// TODO: docs
	case class Custom () extends EventStatus
	
}
