package cc.sukazyo.std
package contexts

/** @since 0.3.0 */
trait AbstractGivenContextContainerOps {
	
	/** The variables that contains in this context.
	  * 
	  * If this object is a context that contains other child/owned contexts, this method will
	  * also count all the variables within the child/owned contexts.
	  * 
	  * @since 0.1.0
	  * 
	  * @see [[AbstractGivenContextGlobalOps.sizeGlobal]] if you are in a global context that
	  *      contains other child/owned context, and you do not want to count the variables in
	  *      child/owned contexts.
	  */
	def size: Int
	
	/** If this context is empty (no variables in here).
	  * 
	  * If this object is a context that contains other child/owned contexts, this method will
	  * also count all the variables within the child/owned contexts.
	  *
	  * @since 0.1.0
	  *
	  * @see [[nonEmpty]] the reversed version.
	  * @see [[AbstractGivenContextGlobalOps.isEmptyGlobal]] if you are in a global context that
	  *      contains other child/owned context, and you do not want to count the variables in
	  *      child/owned contexts.
	  */
	def isEmpty: Boolean
	
	/** If this context is not empty (contains any variables in here).
	  *
	  * If this object is a context that contains other child/owned contexts, this method will
	  * also count all the variables within the child/owned contexts.
	  *
	  * @since 0.1.0
	  *
	  * @see [[isEmpty]] the reversed version.
	  * @see [[AbstractGivenContextGlobalOps.nonEmptyGlobal]] if you are in a global context that
	  *      contains other child/owned context, and you do not want to count the variables in
	  *      child/owned contexts.
	  */
	def nonEmpty: Boolean
	
}
