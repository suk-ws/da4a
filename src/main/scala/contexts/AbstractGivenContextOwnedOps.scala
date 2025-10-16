package cc.sukazyo.std
package contexts

import scala.reflect.ClassTag

trait AbstractGivenContextOwnedOps {
	
	def isOwnedBy [OT: ClassTag]: Boolean
	def isOwnedBy (clazz: Class[?]): Boolean
	
}
