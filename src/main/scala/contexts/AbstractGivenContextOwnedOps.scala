package cc.sukazyo.std
package contexts

import scala.reflect.ClassTag

/** @since 0.3.0 */
trait AbstractGivenContextOwnedOps {
	
	/** If this owned scope is owned by the given class.
	  *
	  * That means if that given class is the owner of this owned scope, you can use
	  * `cxt.ownedBy[Class]` or `cxt / classOf[Class]` to get this scope (more specifically,
	  * to get an [[GivenContext.OwnedContext]] object with the same ability with this object).
	  *
	  * @since 0.1.0
	  */
	def isOwnedBy [OT: ClassTag]: Boolean
	
	/** If this owned scope is owned by the given class.
	  *
	  * That means if that given class is the owner of this owned scope, you can use
	  * `cxt.ownedBy[Class]` or `cxt / classOf[Class]` to get this scope (more specifically,
	  * to get an [[GivenContext.OwnedContext]] object with the same ability with this object).
	  *
	  * @since 0.1.0
	  */
	def isOwnedBy (clazz: Class[?]): Boolean
	
}
