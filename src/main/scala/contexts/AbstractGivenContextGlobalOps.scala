package cc.sukazyo.std
package contexts

import scala.reflect.ClassTag

trait AbstractGivenContextGlobalOps {
	
	def sizeGlobal: Int
	def isEmptyGlobal: Boolean
	def nonEmptyGlobal: Boolean
	
	def ownedScopes: List[AbstractGivenContextOwnedContext]
	
	def / (owner: Class[?]): AbstractGivenContextOwnedContext
	def / (owner: AnyRef): AbstractGivenContextOwnedContext
	def ownedBy [O: ClassTag]: AbstractGivenContextOwnedContext
	def ownedBy [O] (clazz: Class[O]): AbstractGivenContextOwnedContext
	
}
