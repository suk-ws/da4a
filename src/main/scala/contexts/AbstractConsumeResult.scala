package cc.sukazyo.std
package contexts

import contexts.GivenContext.ContextNotGivenException

/** The result context of [[GivenContext.consuming]] operation.
  *
  * Gives a way to do the most useful following operation after the [[GivenContext.consuming]].
  * Mostly depends on whether the consumer function executes successfully or not.
  *
  * Notice that once the consumer function is called, is seen as succeed . The exception
  * throws in the original consumer function will be just throws to the caller, not
  * handled by this context.
  *
  * *Note: Although this trait comes from v0.3.0 update, the implementation in GivenContext
  * actually exists already in v0.1.0.
  *
  * @tparam U The type of the return value that should returns by [[GivenContext.consuming]]'s
  *           consumer function.
  *
  * @since 0.3.0
  */
trait AbstractConsumeResult [U] {
	
	/** Returns the result of the consumer function.
	  *
	  * If the consumer fails to execute for any reason, this function will return [[None]].
	  * Most likely when the required context variable is not exists.
	  *
	  * If the consumer executes successfully, this function will return [[Some]] of the
	  * returned value that consumer function returns.
	  *
	  * @see [[|?]] the operation version
	  * @since 0.1.0
	  */
	def toOption: Option[U]
	
	/** The result of the consumer function in [[Either]] format.
	  *
	  * If the consumer fails to execute for any reason (mostly the required context variable
	  * is not exists), this function will return [[Left]] of the [[ContextNotGivenException]].
	  * The consumer throws exception will just be throws to the caller, not handled by this
	  * method.
	  *
	  * @return [[Right]] of the consumer function returns value, or [[Left]] of the exceptions
	  *         while try to run the consumer function.
	  *
	  * @since 0.2.0
	  */
	def toEither: Either[ContextNotGivenException, U]
	
	/** Returns the result of the consumer function.
	  *
	  * If the consumer fails to execute for any reason, this function will return [[None]].
	  * Most likely when the required context variable is not exists.
	  *
	  * If the consumer executes successfully, this function will return [[Some]] of the
	  * returned value that consumer function returns.
	  *
	  * @see [[toOption]] the method version
	  * @since 0.1.0
	  */
	def |? : Option[U]
	
	/** Execute the provided orElse function if this original consumer function failed.
	  *
	  * This is a short-circuit operator, means if the original consumer function runs
	  * successfully, then the orElse function will never be called.
	  *
	  * @param processor the orElse function that will be called if this original consumer
	  *                  function failed.
	  *
	  * @return Returns the value returned by the original consumer function if it was
	  *         successful, or the orElse function's return value if the original consumer
	  *         function failed.
	  *
	  * @see [[||]] the operation version
	  * @since 0.1.0
	  */
	def orElse[P] (processor: => P): U | P
	
	/** Execute the provided orElse function if this original consumer function failed.
	  *
	  * This is a short-circuit operator, means if the original consumer function runs
	  * successfully, then the orElse function will never be called.
	  *
	  * @param processor the orElse function that will be called if this original consumer
	  *                  function failed.
	  *
	  * @return Returns the value returned by the original consumer function if it was
	  *         successful, or the orElse function's return value if the original consumer
	  *         function failed.
	  *
	  * @see [[orElse]] the method version
	  * @since 0.1.0
	  */
	def ||[P] (processor: => P): U | P
	
	/** Get the result of this consumer function, or [[Null]] if the consumer cannot execute
	  * in some reason (most likely when the required context variable is not exists).
	  *
	  * This is relatively equals to `this.toOption.orNull`, or `this.orElse(null)`.
	  *
	  * @return The consumer function returns value itself, or `null` value.
	  *
	  * @since 0.2.0
	  */
	def orNull: U | Null
	
	/** Get the result of this consumer function, or just throws an exception if the consumer
	  * cannot execute in some reason (most likely when the required context variable is not
	  * exists).
	  *
	  * @throws ContextNotGivenException if the consumer cannot execute due to the required
	  *                                  context parameter is not exists in this context.
	  * @return The consumer function returns value itself.
	  *
	  * @since 0.2.0
	  */
	// TODO: docs and tests
	@throws[ContextNotGivenException]
	def ensureSuccess: U
	
}
