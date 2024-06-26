package cc.sukazyo.std
package contexts

import contexts.GivenContext.*
import stacks.WithCurrentStack

import scala.collection.mutable
import scala.reflect.{classTag, ClassTag}

object GivenContext {
	
	case class FolderClass (clazz: Option[Class[?]])
	object FolderClass:
		def default: FolderClass = FolderClass(None)
	case class RequestItemClass (clazz: Class[?])
	
	class ContextNotGivenException (using
		val requestItemClass: RequestItemClass,
		val folderClass: FolderClass = FolderClass.default,
		val requestStack: StackTraceElement = WithCurrentStack.getStackHeadBeforeClass[GivenContext]
	) extends NoSuchElementException (
		s"None of the ${requestItemClass.clazz.getSimpleName} is in the context${folderClass.clazz.map(" and owned by " + _.getSimpleName).getOrElse("")}, which is required by $requestStack."
	)
	
	type CxtOption[T] = Either[ContextNotGivenException, T]
	
}

/** A mutable collection that can store(provide) any typed value and read(use/consume) that value by type.
  *
  * @example {{{
  *     val cxt = GivenContext()
  *     class BaseClass {}
  *     class MyImplementation extends BaseClass {}
  *
  *
  *     cxt.provide(true)                                    // this provides a Boolean
  *     cxt.provide[BaseClass](new MyImplementation())       // although this object is of type MyImplementation, but it is stored
  *                                                          // as BaseClass so you can (and actually can only) read it using BaseClass
  *     cxt << "string"
  *     cxt << classOf[Int] -> 1                             // you can also manually set the stored type using this method
  *
  *
  *     cxt >> { (i: Int) => println(i) } || { println("no Int data in the context") }
  *     val bool =
  *         cxt.use[String, Boolean] { s => println(s); true } || { false }       // when using .use, the return value must declared
  *     cxt.consume[String] { s => println(s) }                                   // you can use .consume if you don't care the return
  *                                                                               // and this will return a cxt.ConsumeResult[Any]
  *     val cxtResultOpt =                                                        // use toOption if you do not want fallback calculation
  *         cxt.use[Int, String](int => s"int: $int").toOption                    // this returns Option[String]
  *     val cxtResultOpt2 =
  *         cxt >> { (int: Int) => s"int: $int" } |?                              // this returns Option[String] too
  *     //  cxt >> { (int: Int) => cxt >> { (str: String) => { str + int } } } |? // this below is not good to use due to .flatUse
  *                                                                               // is not supported yet. It will return a
  *                                                                               // cxt.ConsumeResult[Option[String]] which is very bad
  *
  *     try {                                                // for now, you can use this way to use multiple data
  *         val int = cxt.use[Int]                           // this returns CxtOption[Int] which is Either[ContextNotGivenException, Int]
  *             .toTry.get
  *         val str = cxt >> classOf[String] match           // this >> returns the same with the .use above
  *             case Right(s) => s
  *             case Left(err) => throw err                  // this is ContextNotGivenException
  *         val bool = cxt >!> classOf[Boolean]              // the easier way to do the above
  *     } catch case e: ContextNotGivenException =>          // if any of the above val is not available, it will catch the exception
  *         e.printStackTrace()
  * }}}
  *
  * @since 0.1.0
  */
//noinspection NoTargetNameAnnotationForOperatorLikeDefinition
class GivenContext {
	
	private type ImplicitsMap [T <: Any] = mutable.HashMap[Class[?], T]
	
	private val variables: ImplicitsMap[Any] = mutable.HashMap.empty
	private val variablesWithOwner: ImplicitsMap[ImplicitsMap[Any]] = mutable.HashMap.empty
	
	/** The total context variables count only in the global scopes.
	  *
	  * @since 0.1.0
	  */
	def sizeGlobal: Int = variables.size
	
	/** If contexts in the global scope is empty.
	  *
	  * Indicates that there is no context variables in the global scope.
	  *
	  * @since 0.1.0
	  * @see [[nonEmptyGlobal]] : the reversed version
	  */
	def isEmptyGlobal: Boolean = variables.isEmpty
	
	/** If contexts in the global scope is not empty.
	  *
	  * @since 0.1.0
	  * @see [[isEmptyGlobal]] : the reversed version
	  */
	def nonEmptyGlobal: Boolean = variables.nonEmpty
	
	/** The total context variables count in all scopes.
	  *
	  * This is the sum of [[sizeGlobal]] and all the [[ownedScopes]]'s[[OwnedContext.size]].
	  *
	  * @since 0.1.0
	  */
	def size: Int =
		sizeGlobal + ownedScopes.map(_.size).sum
	
	/** If current given context container is empty.
	  *
	  * If and if only the global scope and all the owned scopes are empty, this is true.
	  *
	  * It does not matter whether the global scopes count is empty or not, means even there's
	  * any owned scopes, if the scope have no context variables, it is still empty, and this
	  * is still true.
	  *
	  * If this is true, then the [[isEmptyGlobal]] must be true and all the [[ownedScopes]]'s
	  * [[OwnedContext.isEmpty]] must be true too.
	  *
	  * @since 0.1.0
	  * @see [[nonEmpty]] : the reversed version
	  */
	def isEmpty: Boolean =
		isEmptyGlobal && ownedScopes.forall(_.isEmpty)
	
	/** If current given context container is not empty.
	  *
	  * Indicates that there's at lease one context variables. It does not matter whether the
	  * variable is in global scope or any one of the owned scopes.
	  *
	  * If there exists owned scopes with no context variables, then it is still empty, and it
	  * is still false.
	  *
	  * @since 0.1.0
	  * @see [[isEmpty]] : the reversed version
	  */
	def nonEmpty: Boolean =
		nonEmptyGlobal || ownedScopes.exists(_.nonEmpty)
	
	/** Returns a list that contains the [[OwnedContext]] instance for each owned scopes.
	  *
	  * This will return all of the owned scopes no matter whether there's any context variables,
	  * means it might contains some owned scopes that have no context variables at all. So that,
	  * if you want to check if there are variables in all the owned scopes, you should iterate
	  * the list instead of just check the size of this returned list.
	  *
	  * @since 0.1.0
	  * @return A list of [[OwnedContext]], if theres no owned scopes, it will be an empty list.
	  */
	def ownedScopes: List[OwnedContext] =
		variablesWithOwner.map((k, v) => new OwnedContext(k)).toList
	
	/** Add one context parameter to the global scope in this [[GivenContext]].
	  *
	  * The parameter's type is its key in the context. It can be get by [[use]] series of
	  * method later by using its type reference. But notice that due to limitation to the jvm
	  * and language, you can and can only use a base [[Class]] as the type. All the generic
	  * types will be erased, like `List[String]` will be stored as just a `List`. So do not
	  * pass a generic typed variable to this method, unless you know what you are doing.
	  *
	  * If you add two or more parameters to the context which have the same type, the last one
	  * will overwrites the previous ones. So take care of adding parameters that have the type
	  * that can be used everywhere, due to it is easy to got a conflict and cause unexpected
	  * overwrites.
	  *
	  * The recommended solution is create your own non-generic `record` type that encapsulate
	  * your data so that you will not occurred any type erasure problem, and conflict problem.
	  *
	  * Another solution to reduce the conflict problem is to use the [[OwnedContext]] to create
	  * your own owned scope so that you can have a isolated scope that will not conflict with
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
	infix def provide [T: ClassTag] (i: T): Unit =
		variables += (classTag[T].runtimeClass -> i)
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
	  *           element is the variable itself that will provided to the global scope.
	  * @tparam T the type of the variable, also will be the key of this context parameter.
	  *
	  * @since 0.1.0
	  */
	infix def << [T: ClassTag] (is: (Class[T], T)): Unit =
		val (_, i) = is
		this.provide[T](i)
	/** Provide a context variable to the global scope. Works exactly the same with the [[provide]]
	  * method.
	  *
	  * This will automatically get the class of the variable as the key of the context variable.
	  *
	  * @see [[provide]]
	  *
	  * @since 0.1.0
	  */
	infix def << [T: ClassTag] (i: T): Unit =
		this.provide[T](i)
	
	/** @since 0.1.0 */
	def use [T: ClassTag]: CxtOption[T] =
		given t: RequestItemClass = RequestItemClass(classTag[T].runtimeClass)
		//noinspection DuplicatedCode
		variables get t.clazz match
			case Some(i) => Right(i.asInstanceOf[T])
			case None => Left(ContextNotGivenException())
	/** @since 0.1.0 */
	infix def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T] match
			case Left(_) => ConsumeFailed[U]()
			case Right(i) => ConsumeSucceed[U](consumer(i))
	/** @since 0.1.0 */
	infix def >> [T: ClassTag] (t: Class[T]): CxtOption[T] =
		this.use[T]
	/** @since 0.1.0 */
	infix def >!> [T: ClassTag] (t: Class[T]): T =
		this.use[T].toTry.get
	/** @since 0.1.0 */
	infix def >>[T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T,U](consumer)
	/** @since 0.1.0 */
	infix def consume [T: ClassTag] (consume: T => Any): ConsumeResult[Any] =
		this.use[T,Any](consume)
	
	/** Get the [[OwnedContext]] connects to the owner class given.
	  *
	  * @since 0.1.0
	  */
	def / (owner: Class[?]): OwnedContext =
		OwnedContext(owner)
	/** Get the [[OwnedContext]] connects to the owner class, which class is the given values
	  * class.
	  *
	  * Notice that given variable's value does not effects the result, the only need information
	  * is that its specific class.
	  *
	  * @since 0.1.0
	  */
	def / (owner: AnyRef): OwnedContext =
		OwnedContext(owner.getClass)
	/** Get the [[OwnedContext]] connects to the owner class.
	  *
	  * @tparam O the owner class
	  *
	  * @since 0.1.0
	  */
	def ownedBy [O: ClassTag]: OwnedContext =
		OwnedContext(classTag[O].runtimeClass)
	
	/** An access helper for a owned context in the [[GivenContext]].
	  *
	  * This connects to an owned context in the [[GivenContext]] and provides access to it.
	  *
	  * Each owned context is separated to another owned context, and also separated to the global
	  * context. So that you can register a new context variables here without worrying about if
	  * it may conflict to the other's registered context variables with the same type, as long
	  * as you set a unique [[Class]] as the owned context's owner (aka. key).
	  *
	  * @param thisClazz the key of the owned context that this helper connects to. Also this is the
	  *                  class which owns the owned context.
	  *
	  *                  since 0.1.0
	  *
	  * @since 0.1.0
	  */
	class OwnedContext (thisClazz: Class[?]) {
		private given folderClass: FolderClass = FolderClass(Some(thisClazz))
		
		private def getThisMap: Option[ImplicitsMap[Any]] =
			variablesWithOwner get thisClazz
		private def getThisMapOrCreate: ImplicitsMap[Any] =
			variablesWithOwner.getOrElseUpdate(
				thisClazz, mutable.HashMap.empty)
		
		/** If this owned scope is owned by the given class.
		  * @since 0.1.0
		  */
		def isOwnedBy [OT: ClassTag]: Boolean =
			isOwnedBy(classTag[OT].runtimeClass)
		/** If this owned scope is owned by the given class.
		  * @since 0.1.0
		  */
		def isOwnedBy (clazz: Class[?]): Boolean =
			thisClazz == clazz
		
		/** Get the count of the context variables in this owned scope.
		  * @since 0.1.0
		  */
		def size: Int = getThisMap.map(_.size).getOrElse(0)
		/** If this owned scope do NOT have any context variables.
		  * @since 0.1.0
		  */
		def isEmpty: Boolean = getThisMap.forall(_.isEmpty)
		/** If this owned scope have any context variables.
		  * @since 0.1.0
		  */
		def nonEmpty: Boolean = getThisMap.exists(_.nonEmpty)
		
		/** @see [[GivenContext.provide]]
		  * @since 0.1.0
		  */
		infix def provide [T: ClassTag] (i: T): Unit =
			getThisMapOrCreate.addOne(classTag[T].runtimeClass -> i)
		/** @see [[GivenContext.<<(i:(Class[T],T))*]]
		  * @since 0.1.0
		  */
		infix def << [T: ClassTag] (is: (Class[T], T)): Unit =
			val (_, i) = is
			this.provide[T](i)
		/** @see [[GivenContext.<<(i:T)*]]
		  * @since 0.1.0
		  */
		infix def << [T: ClassTag] (i: T): Unit =
			this.provide[T](i)
		
		/** @since 0.1.0 */
		def use [T: ClassTag]: CxtOption[T] =
			given t: RequestItemClass = RequestItemClass(classTag[T].runtimeClass)
			variablesWithOwner get thisClazz match
				case Some(varColl) =>
					//noinspection DuplicatedCode
					varColl get t.clazz match
						case Some(i) => Right(i.asInstanceOf[T])
						case None => Left(ContextNotGivenException())
				case None => Left(ContextNotGivenException())
		/** @since 0.1.0 */
		infix def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			use[T] match
				case Left(_) => ConsumeFailed[U]()
				case Right(i) => ConsumeSucceed[U](consumer(i))
		/** @since 0.1.0 */
		infix def >> [T: ClassTag] (t: Class[T]): CxtOption[T] =
			this.use[T]
		/** @since 0.1.0 */
		infix def >!> [T: ClassTag] (t: Class[T]): T =
			this.use[T].toTry.get
		/** @since 0.1.0 */
		infix def >> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			this.use[T,U](consumer)
		/** @since 0.1.0 */
		infix def consume [T: ClassTag] (consume: T => Any): ConsumeResult[Any] =
			this.use[T,Any](consume)
		
	}
	
	/** The result context of [[GivenContext.consume]] operation.
	  *
	  * Gives a way to do the most useful following operation after the [[GivenContext.consume]].
	  * Mostly depends on whether the consumer function executes successfully or not.
	  *
	  * Notice that once the consumer function is called, is is seen as succeed . The exception
	  * throws in the original consumer function will be just throws to the caller, not
	  * handled by this context.
	  *
	  * @tparam U The type of the return value that should returns by [[GivenContext.consume]]'s
	  *           consumer function.
	  *
	  * @since 0.1.0
	  */
	trait ConsumeResult[U] {
		
		/** Returns the result of the consumer function.
		  *
		  * If the consumer fails to execute for any reason, this function will returns [[None]].
		  * Most likely when the required context variable is not exists.
		  *
		  * If the consumer executes successfully, this function will returns [[Some]] of the
		  * returned value that consumer function returns.
		  *
		  * @see [[|?]] the operation version
		  * @since 0.1.0
		  */
		def toOption: Option[U]
		
		/** Returns the result of the consumer function.
		  *
		  * If the consumer fails to execute for any reason, this function will returns [[None]].
		  * Most likely when the required context variable is not exists.
		  *
		  * If the consumer executes successfully, this function will returns [[Some]] of the
		  * returned value that consumer function returns.
		  *
		  * @see [[toOption]] the method version
		  * @since 0.1.0
		  */
		def |? : Option[U] = toOption
		
		/** Execute the provided orElse function if this original consumer function failed.
		  *
		  * This is a short-circuit operator, means if the original consumer function runs
		  * successfully, then the orElse function will never be called.
		  *
		  * @param processor the orElse function that will be called if this original consumer
		  *                  function failed.
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
		  * @return Returns the value returned by the original consumer function if it was
		  *         successful, or the orElse function's return value if the original consumer
		  *         function failed.
		  *
		  * @see [[orElse]] the method version
		  * @since 0.1.0
		  */
		def ||[P] (processor: => P): U | P = orElse(processor)
		
	}
	private class ConsumeSucceed[U] (succeedValue: U) extends ConsumeResult[U]:
		private def get: U = succeedValue
		override def toOption: Some[U] = Some(get)
		override def orElse[P] (processor: =>P): U|P = get
	private class ConsumeFailed[U] extends ConsumeResult[U]:
		override def toOption: None.type = None
		override def orElse[P] (processor: =>P): U|P = processor
	
}
