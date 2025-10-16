package cc.sukazyo.std
package contexts

import scala.reflect.ClassTag

/** @since 0.3.0 */
trait AbstractGivenContextGlobalOps {
	
	/** The total context variables count only in the global scopes.
	  *
	  * Only variables that can directly got using `get` method in this context (that means no more
	  * [[ownedBy]] need to be called) is counted. All the variables in owned scopes or child
	  * scopes is not considered.
	  *
	  * @since 0.1.0
	  * @see [[AbstractGivenContextContainerOps.size]] If you want to count all variables that
	  *      contains in current scope and child/owned scopes.
	  */
	def sizeGlobal: Int
	
	/** If contexts in the global scope is empty.
	  *
	  * Indicates that there is no context variables in the global scope.
	  *
	  * Only variables that can directly got using `get` method in this context (that means no more
	  * [[ownedBy]] need to be called) is counted. All the variables in owned scopes or child
	  * scopes is not considered.
	  *
	  * @since 0.1.0
	  * @see [[nonEmptyGlobal]] : the reversed version
	  * @see [[AbstractGivenContextContainerOps.nonEmpty]] If you want to count all variables that
	  *      contains in current scope and child/owned scopes.
	  */
	def isEmptyGlobal: Boolean
	
	/** If contexts in the global scope is not empty.
	  *
	  * Only variables that can directly got using `get` method in this context (that means no more
	  * [[ownedBy]] need to be called) is counted. All the variables in owned scopes or child
	  * scopes is not considered.
	  *
	  * @since 0.1.0
	  * @see [[isEmptyGlobal]] : the reversed version
	  * @see [[AbstractGivenContextContainerOps.nonEmpty]] If you want to count all variables that
	  *      contains in current scope and child/owned scopes.
	  */
	def nonEmptyGlobal: Boolean
	
	
	
	/** Get a list that contains the [[GivenContext.OwnedContext]] instance for each owned
	  * scopes.
	  *
	  * This will return all the owned scopes no matter whether there's any context variables,
	  * means it might contain some owned scopes that have no context variables at all. So that,
	  * if you want to check if there are variables in all the owned scopes, you should iterate
	  * the list instead of just check the size of this returned list.
	  *
	  * @since 0.1.0
	  * @return A list of [[GivenContext.OwnedContext]], if there's no owned scopes, it will be
	  *         an empty list.
	  */
	def ownedScopes: List[AbstractGivenContextOwnedContext]
	
	
	
	/** Get the [[GivenContext.OwnedContext]] connects to the owner class given.
	  *
	  * @since 0.1.0
	  */
	def / (owner: Class[?]): AbstractGivenContextOwnedContext
	
	/** Get the [[GivenContext.OwnedContext]] connects to the owner class, which class is the
	  * given values class.
	  *
	  * Notice that given variable's value does not affect the result, the only need information
	  * is that its specific class.
	  *
	  * @since 0.1.0
	  */
	def / (owner: AnyRef): AbstractGivenContextOwnedContext
	
	/** Get the [[GivenContext.OwnedContext]] connects to the owner class.
	  *
	  * @tparam O the owner class
	  *
	  * @since 0.1.0
	  */
	def ownedBy [O: ClassTag]: AbstractGivenContextOwnedContext
	
	/** Get the [[GivenContext.OwnedContext]] connects to the owner class.
	  *
	  * @tparam O the owner class
	  *
	  * @since 0.1.0
	  */
	def ownedBy [O] (clazz: Class[O]): AbstractGivenContextOwnedContext
	
}
