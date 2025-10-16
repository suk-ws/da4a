package cc.sukazyo.std
package contexts

/** An access helper for an owned context in the [[GivenContext]].
  *
  * This connects to an owned context in the [[GivenContext]] and provides access to it.
  *
  * Each owned context is separated to another owned context, and also separated to the global
  * context. So that you can register a new context variables here without worrying about if
  * it may conflict to the other's registered context variables with the same type, as long
  * as you set a unique [[Class]] as the owned context's owner (aka. key).
  *
  * *Note: Although this trait comes from v0.3.0 update, the implementation in GivenContext
  * actually exists already in v0.1.0.
  *
  * @since 0.3.0
  */
trait AbstractGivenContextOwnedContext
	extends AbstractGivenContextOwnedOps
		with AbstractGivenContextVariableOps
		with AbstractGivenContextContainerOps
