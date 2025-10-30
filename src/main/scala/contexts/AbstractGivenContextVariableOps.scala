package cc.sukazyo.std
package contexts

import contexts.AbstractGivenContextVariableOps.*
import contexts.GivenContext.ContextNotGivenException

import java.util.function.Consumer
import scala.reflect.ClassTag

/** @since 0.3.0 */
trait AbstractGivenContextVariableOps
	extends Helpers
		with ReadOnlyOps
		with ProvideOps
		with DiscardOps
		with PopOps
		with TakeOps
		with AcceptOps
		with DrainOps

/** @since 0.3.0 */
object AbstractGivenContextVariableOps extends Helpers {
	
	/** @since 0.3.0 */
	trait ReadOnlyOps extends Helpers
		with GetOps
		with UseOps
		with HasOps
		with ContainsOps
	
	/** Common things (mostly type aliases) that related to variable operation.
	  * 
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
	trait Helpers {
		
		/** Either a [[ContextNotGivenException]] when trying to get an unexisting variable, or
		  * the variable itself.
		  *
		  * Used in many `get` related method of [[GivenContext]].
		  *
		  * @since 0.3.0
		  */
		type CxtOption[T] = Either[ContextNotGivenException, T]
		
	}
	
	/** The definition of methods that aims to provide/store/save a variable to
	  * [[GivenContext]].
	  * 
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  * 
	  * @since 0.3.0
	  */
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
	
	/** The definition of methods that aims to remove/delete/discard/reset/clean a variable in
	  * [[GivenContext]].
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
	trait DiscardOps {
		
		/** @since 0.3.0 */
		def discard [T] (clazz: Class[T]): Boolean
		
		/** @since 0.3.0 */
		def discard [T: ClassTag]: Boolean
		
	}
	
	/** The definition of methods that aims to get a variable from [[GivenContext]].
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
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
	
	/** The definition of methods that aims to get a variable from [[GivenContext]], in a
	  * callback form.
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
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
	
	/** The definition of methods that aims to do a [[GetOps get]] and
	  * [[DiscardOps delete(discard)]] in the same time.
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
	trait PopOps {
		
		/** @since 0.3.0 */
		def pop [T] (clazz: Class[T]): CxtOption[T]
		/** @since 0.3.0 */
		def pop [T: ClassTag]: CxtOption[T]
		/** @since 0.3.0 */
		infix def !>> [T] (clazz: Class[T]): CxtOption[T]
		
		/** @since 0.3.0 */
		def popOrNull [T] (clazz: Class[T]): T | Null
		/** @since 0.3.0 */
		def popOrNull [T: ClassTag]: T | Null
		/** @since 0.3.0 */
		infix def !>?> [T] (clazz: Class[T]): T | Null
		
		/** @since 0.3.0 */
		@throws[ContextNotGivenException]
		def popUnsafe [T] (clazz: Class[T]): T
		/** @since 0.3.0 */
		@throws[ContextNotGivenException]
		def popUnsafe [T: ClassTag]: T
		/** @since 0.3.0 */
		@throws[ContextNotGivenException]
		infix def !>!> [T] (clazz: Class[T]): T
		
	}
	
	/** The definition of methods that aims to do [[PopOps]] in a callback form.
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
	trait TakeOps {
		
		/** @since 0.3.0 */
		def take [T, U] (clazz: Class[T])(consumer: T => U): AbstractConsumeResult[U]
		/** @since 0.3.0 */
		def take [T: ClassTag, U] (consumer: T => U): AbstractConsumeResult[U]
		/** @since 0.3.0 */
		infix def !>> [T: ClassTag, U] (consumer: T => U): AbstractConsumeResult[U]
		
	}
	
	/** The definition of methods that aims to check whether any variable with one type is
	  * provided.
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
	trait HasOps {
		
		/** @since 0.3.0 */
		def has [T] (clazz: Class[T]): Boolean
		/** @since 0.3.0 */
		def has [T: ClassTag]: Boolean
		/** @since 0.3.0 */
		def has [T: ClassTag] (i: T): Boolean
		
		/** @since 0.3.0 */
		infix def ?# [T] (clazz: Class[T]): Boolean
		/** @since 0.3.0 */
		infix def ?# [T: ClassTag] (i: T): Boolean
		
	}
	
	/** The definition of methods that aims to check whether any variable with one type is
	  * provided, and remove the matched variable if presents.
	  *
	  * This may look like the [[HasOps]] with [[PopOps]] features, or the [[DiscardOps]] with a
	  * fancy way.
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
	trait AcceptOps {
		
		/** @since 0.3.0 */
		def accept [T] (clazz: Class[T]): Boolean
		/** @since 0.3.0 */
		def accept [T: ClassTag]: Boolean
		/** @since 0.3.0 */
		def accept [T: ClassTag] (i: T): Boolean
		
		/** @since 0.3.0 */
		infix def ?#- [T] (clazz: Class[T]): Boolean
		/** @since 0.3.0 */
		infix def ?#- [T: ClassTag] (i: T): Boolean
		
	}
	
	/** The definition of methods that aims to check whether any variable with one type is
	  * provided, and the variable in context equals the checking variable.
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
	trait ContainsOps {
		
		/** @since 0.3.0 */
		def contains [T: ClassTag] (value: T): Boolean
		/** @since 0.3.0 */
		infix def ?* [T: ClassTag] (value: T): Boolean
		
	}
	
	/** The definition of methods that aims to check whether any variable with one type is
	  * provided, and the variable in context equals the checking variable. If all of these
	  * matches, the variable will also be deleted.
	  *
	  * The combination of [[ContainsOps]] and [[PopOps]]
	  *
	  * For many usage, you should not extend this trait but to use
	  * [[AbstractGivenContextVariableOps]]. This trait may move to another location in newer
	  * version.
	  *
	  * @since 0.3.0
	  */
	trait DrainOps {
		
		/** @since 0.3.0 */
		def drain [T: ClassTag] (value: T): Boolean
		/** @since 0.3.0 */
		infix def ?- [T: ClassTag] (value: T): Boolean
		
	}
	
}
