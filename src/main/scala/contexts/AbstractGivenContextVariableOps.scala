package cc.sukazyo.std
package contexts

import contexts.AbstractGivenContextVariableOps.{GetOps, Helpers, ProvideOps, UseOps}
import contexts.GivenContext.ContextNotGivenException

import java.util.function.Consumer
import scala.reflect.ClassTag

trait AbstractGivenContextVariableOps
	extends Helpers
		with ProvideOps
//		with DiscardOps
		with GetOps
		with UseOps
//		with PopOps
//		with TakeOps

object AbstractGivenContextVariableOps extends Helpers {
	
	trait Helpers {
		
		type CxtOption[T] = Either[ContextNotGivenException, T]
		
	}
	
	trait ProvideOps {
		
		/** @since 0.2.0 */
		infix def provide[T] (clazz: Class[T], i: T): Unit // TODO: docs and tests
		
		/** Add one context parameter to the global scope in this [[GivenContext]].
		  *
		  * The parameter's type is its key in the context. It can be got by [[use]] series of
		  * method later by using its type reference. But notice that due to limitation to the jvm
		  * and language, you can and can only use a base [[Class]] as the type. All the generic
		  * types will be erased, like `List[String]` will be stored as just a `List`. So do not
		  * pass a generic typed variable to this method, unless you know what you are doing.
		  *
		  * If you add two or more parameters to the context which have the same type, the last one
		  * will overwrite the previous ones. So take care of adding parameters that have the type
		  * that can be used everywhere, due to it is easy to get a conflict and cause unexpected
		  * overwrites.
		  *
		  * The recommended solution is to creates your own non-generic `record` type that encapsulate
		  * your data so that you will not get any type erasure problem, and conflict problem.
		  *
		  * Another solution to reduce the conflict problem is to use the [[OwnedContext]] to create
		  * your own owned scope so that you can have an isolated scope that will not conflict with
		  * the context variables that in the global scope.
		  *
		  * @param i the variable that will be stored as a context parameter in the global scope.
		  * @tparam T the type of the variable, also will be the key of this context parameter.
		  *
		  *           This usually be the exact type of the variable. But you can also provide
		  *           another type explicitly, so that the explicitly provided type will be the
		  *           key of the context parameter but not the exact variable's type. This may be
		  *           useful when you want to add a class's children class implementation but want
		  *           to keep the variable is shown as the parent types.
		  *
		  * @since 0.1.0
		  */
		infix def provide[T: ClassTag] (i: T): Unit
		
		/** Provide a context variable to the global scope, with the variable key is the given
		  * class.
		  *
		  * this works exactly the same with the [[provide]] method except the differences in
		  * params.
		  *
		  * @see [[provide]]
		  *
		  * @param is A tuple with two elements: The first element is the Class instance indicates
		  *           the class that will be the key of the given context parameter; The second
		  *           element is the variable itself that will be provided to the global scope.
		  * @tparam T the type of the variable, also will be the key of this context parameter.
		  *
		  * @since 0.1.0
		  */
		infix def <<[T: ClassTag] (is: (Class[T], T)): Unit
		
		/** Provide a context variable to the global scope. Works exactly the same with the [[provide]]
		  * method.
		  *
		  * This will automatically get the class of the variable as the key of the context variable.
		  *
		  * @see [[provide]]
		  * @since 0.1.0
		  */
		infix def <<[T: ClassTag] (i: T): Unit
		
	}
	
//	trait DiscardOps {}
	
	trait GetOps {
		
		/** @since 0.1.0
		  * @deprecated For more complex use cases with less conflict, use [[get]] instead.
		  */
		@deprecated("Use get instead.", "da4a 0.2.0")
		def use[T: ClassTag]: CxtOption[T]
		
		/** @since 0.2.0 */
		infix def get[T] (clazz: Class[T]): CxtOption[T] // TODO: docs and tests
		
		/** @since 0.2.0 */
		def get[T: ClassTag]: CxtOption[T] // TODO: docs and tests
		
		/** @since 0.1.0 */
		infix def >>[T] (t: Class[T]): CxtOption[T]
		
		/// #block getOrNull
		///   get one, or returns null
		
		/** @since 0.2.0 */
		infix def getOrNull[T] (clazz: Class[T]): T | Null // TODO: docs and tests
		
		/** @since 0.2.0 */
		def getOrNull[T: ClassTag]: T | Null // TODO: docs and tests
		
		/** @since 0.2.0 */
		infix def >?>[T] (t: Class[T]): T | Null // TODO: docs and tests
		
		/// #block unsafeGet
		///   get one, or throws an exception
		
		/** @since 0.2.0 */
		@throws[ContextNotGivenException]
		infix def getUnsafe[T] (clazz: Class[T]): T // TODO: docs and tests
		
		/** @since 0.2.0 */
		@throws[ContextNotGivenException]
		def getUnsafe[T: ClassTag]: T // TODO: docs and tests
		
		/** @since 0.1.0 */
		@throws[ContextNotGivenException]
		infix def >!>[T] (t: Class[T]): T
		
	}
	
	trait UseOps {
		
		/** @since 0.2.0 */
		def use[T, U] (clazz: Class[T])(consumer: T => U): AbstractConsumeResult[U] // TODO: docs and tests
		
		/** @since 0.1.0 */
		infix def use[T: ClassTag, U] (consumer: T => U): AbstractConsumeResult[U]
		
		/** @since 0.1.0 */
		infix def >>[T: ClassTag, U] (consumer: T => U): AbstractConsumeResult[U]
		
		/** @since 0.2.0 */
		def consume[T] (clazz: Class[T])(consumer: T => Any): AbstractConsumeResult[Any] // TODO: docs and tests
		
		/** @since 0.1.0 */
		infix def consume[T: ClassTag] (consumer: T => Any): AbstractConsumeResult[Any]
		
		/** @since 0.2.0 */
		def consuming[T] (clazz: Class[T])(jConsumer: Consumer[T]): AbstractConsumeResult[Unit] // TODO: docs and tests
		
	}
	
//	trait PopOps {}
	
//	trait TakeOps {}
	
}
