package cc.sukazyo.std
package event

/** todo: docs
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
trait RichEvent [EP, ER]
	extends AbstractEvent[EP, ER]
		with SimpleEventOps[EP, ER]
		with AbstractRichEvent[EP, ER]
		with RichEventOps[EP, ER]
		with RichEmittableEvent[EP, ER]
