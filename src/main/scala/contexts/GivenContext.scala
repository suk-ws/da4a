package cc.sukazyo.std
package contexts

import contexts.GivenContext.*
import stacks.WithCurrentStack

import java.util.function.Consumer as JConsumer
import scala.collection.mutable
import scala.jdk.FunctionConverters.*
import scala.reflect.{classTag, ClassTag}

/** The constructor and inner classes of [[GivenContext]].
  * 
  * @since 0.1.0
  */
object GivenContext {
	
	/** Create a new [[GivenContext]].
	  *
	  * @since 0.2.0
	  */
	def apply (): GivenContext =
		new GivenContext(mutable.HashMap.empty, mutable.HashMap.empty)
	
	/** Clone a new [[GivenContext]] from an old one.
	  *
	  * @see [[GivenContext.clone]]
	  *
	  * @since 0.2.0
	  */
	def from (source: GivenContext): GivenContext =
		source.clone()
	
	private type ImplicitsMap [T <: Any] = mutable.HashMap[Class[?], T]
	
	/** @since 0.1.0 */
	case class FolderClass (clazz: Option[Class[?]])
	/** @since 0.1.0 */
	object FolderClass:
		/** @since 0.1.0 */
		def default: FolderClass = FolderClass(None)
	/** @since 0.1.0 */
	case class RequestItemClass (clazz: Class[?])
	
	/** There are no requested item in current [[GivenContext]].
	  *
	  * @param cxt The [[GivenContext]] that this request is on.
	  * @param requestItemClass The requesting item's [[Class]] tag.
	  * @param folderClass The requesting item's owner [[OwnedContext]]'s [[Class]] tag.
	  *                    If the requesting owned scope is the global scope, this is `None`,
	  *                    or else this will be a [[Some]] of a [[Class]].
	  * @param requestStack where this request is sent from.
	  *
	  * @since 0.1.0
	  */
	class ContextNotGivenException (using
		val cxt: GivenContext,
		val requestItemClass: RequestItemClass,
		val folderClass: FolderClass = FolderClass.default,
		val requestStack: StackTraceElement = WithCurrentStack.getStackHeadBeforeClass[GivenContext]
	) extends NoSuchElementException (
		s"None of the ${requestItemClass.clazz.getSimpleName} is in the context${folderClass.clazz.map(" and owned by " + _.getSimpleName).getOrElse("")}, which is required by $requestStack."
	)
	
	/** The return type of [[GivenContext.get]], contains [[Either]] the got value as [[Right]],
	  * or a [[ContextNotGivenException]] as [[Left]].
	  * @since 0.1.0
	  */
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
class GivenContext private (
	private val variables: ImplicitsMap[Any],
	private val variablesWithOwner: ImplicitsMap[ImplicitsMap[Any]]
) extends mutable.Cloneable[GivenContext] {
	
	given GivenContext = this
	
	/** Create a brand new [[GivenContext]].
	  *
	  * @since 0.1.0
	  * @deprecated This is only for source/binary capability purpose. You should use the simple
	  *             factory method [[GivenContext.apply]] instead.
	  */
	@deprecated("Use GivenContext.apply() instead", "da4a 0.2.0")
	def this () =
		this(mutable.HashMap.empty, mutable.HashMap.empty)
	
	/** Create a shallow copy of current [[GivenContext]] instance, with the same data, but
	  * operating on the returned copy will not affect current instance or other copy.
	  *
	  * Notice that this is a shallow copy, means it only copies which class is referenced to
	  * which object, it will not copy the object itself. So that, if the object itself is
	  * immutable, changing the object will make other copies referenced to it change too.
	  *
	  * @example
	  * {{{
	  *     val oldContext = GivenContext()
	  *     oldContext.provide[Int](1)
	  *     oldContext.use[Int] // will be 1
	  *     val newContext = oldContext.clone()
	  *     newContext.use[Int] // will be 1, copied from the oldContext
	  *
	  *     newContext.provide[Int](128)
	  *     newContext.use[Int] // now will be 128 due to it has changed
	  *     oldContext.use[Int] // will be still 1
	  *
	  *     oldContext.provide[ListBuffer[?]] = ListBuffer("old value")
	  *     oldContext.use[ListBuffer[?]] // will be ListBuffer("old value")
	  *     val brandNewContext = oldContext.clone()
	  *     brandNewContext.use[ListBuffer[String]].addOne("new value")
	  *     brandNewContext.use[ListBuffer[?]] // will be ListBuffer("old value", "new value")
	  *     oldContext.use[ListBuffer[?]] // will be ListBuffer("old value", "new value"), due
	  *                                   // to the oldContext and brandNewContext is all referenced
	  *                                   // to the same ListBuffer
	  *
	  *     oldContext.provide[ListBuffer[?]] = ListBuffer("new buffer")
	  *     oldContext.use[ListBuffer[String]].addOne("Hello World!")
	  *     oldContext.use[ListBuffer[?]] // will be ListBuffer("new buffer", "Hello World")
	  *     brandNewContext.use[ListBuffer[?]] // will be ListBuffer("old value", "new value"),
	  *                                        // due to the oldContext and brandNewContext now
	  *                                        // referenced to different ListBuffers
	  *
	  * }}}
	  *
	  * @return A shallow copy of the current [[GivenContext]].
	  *
	  * @since 0.2.0
	  */
	override def clone (): GivenContext =
		new GivenContext(
			variables.map(_ -> _),
			variablesWithOwner.map(_ -> _.map(_ -> _))
		)
	
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
	
	/** @since 0.2.0 */
	infix def provide [T] (clazz: Class[T], i: T): Unit = // TODO: docs and tests
		variables += (clazz -> i)
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
		this.provide(classTag[T].runtimeClass.asInstanceOf[Class[T]], i)
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
	
	/// #block get
	///   get one to a CxtOption
	/** @since 0.1.0
	  * @deprecated For more complex use cases with less conflict, use [[get]] instead.
	  */
	@deprecated("Use get instead.", "da4a 0.2.0")
	def use [T: ClassTag]: CxtOption[T] = this.get
	/** @since 0.2.0 */
	infix def get [T] (clazz: Class[T]): CxtOption[T] = // TODO: docs and tests
		given t: RequestItemClass = RequestItemClass(clazz)
		//noinspection DuplicatedCode
		variables get t.clazz match
			case Some(i) => Right(i.asInstanceOf[T])
			case None => Left(ContextNotGivenException())
	/** @since 0.2.0 */
	def get [T: ClassTag]: CxtOption[T] = // TODO: docs and tests
		this.get(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	/** @since 0.1.0 */
	infix def >> [T] (t: Class[T]): CxtOption[T] =
		this.get(t)
	
	/// #block getOrNull
	///   get one, or returns null
	/** @since 0.2.0 */
	infix def getOrNull [T] (clazz: Class[T]): T | Null = // TODO: docs and tests
		this.get(clazz)
			.getOrElse(null)
	/** @since 0.2.0 */
	def getOrNull [T: ClassTag]: T | Null = // TODO: docs and tests
		this.getOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	/** @since 0.2.0 */
	infix def >?> [T] (t: Class[T]): T | Null = // TODO: docs and tests
		this.getOrNull(t)
	
	/// #block unsafeGet
	///   get one, or throws an exception
	/** @since 0.2.0 */
	@throws[ContextNotGivenException]
	infix def getUnsafe [T] (clazz: Class[T]): T = // TODO: docs and tests
		this.get(clazz)
			.toTry.get
	/** @since 0.2.0 */
	@throws[ContextNotGivenException]
	def getUnsafe [T: ClassTag]: T = // TODO: docs and tests
		this.getUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	/** @since 0.1.0 */
	@throws[ContextNotGivenException]
	infix def >!> [T] (t: Class[T]): T =
		this.getUnsafe(t)
	
	/// #block use
	///   directly use one by consumer
	/** @since 0.2.0 */
	def use [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] = // TODO: docs and tests
		this.get[T](clazz) match
			case Left(e) => ConsumeFailed[U](e)
			case Right(i) => ConsumeSucceed[U](consumer(i))
	/** @since 0.1.0 */
	infix def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T,U](classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
	/** @since 0.1.0 */
	infix def >> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T,U](consumer)
	/** @since 0.2.0 */
	def consume [T] (clazz: Class[T])(consumer: T => Any): ConsumeResult[Any] = // TODO: docs and tests
		this.use[T,Any](clazz)(consumer)
	/** @since 0.1.0 */
	infix def consume [T: ClassTag] (consumer: T => Any): ConsumeResult[Any] =
		this.use[T,Any](consumer)
	/** @since 0.2.0 */
	def consuming [T] (clazz: Class[T])(jConsumer: JConsumer[T]): ConsumeResult[Unit] = // TODO: docs and tests
		this.use[T,Unit](clazz)(jConsumer.asScala)
	
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
	/** @since 0.2.0 */
	def ownedBy [O] (clazz: Class[O]): OwnedContext =
		OwnedContext(clazz)
	
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
	class OwnedContext (thisClazz: Class[?]) { // TODO: add java capability methods
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
		
		/** @see [[GivenContext.provide(Class[T],T)*]]
		  * @since 0.2.0 */
		infix def provide [T] (clazz: Class[T], i: T): Unit = // TODO: tests
			getThisMapOrCreate.addOne(clazz -> i)
		/** @see [[GivenContext.provide]]
		  * @since 0.1.0
		  */
		infix def provide [T: ClassTag] (i: T): Unit =
			this.provide(classTag[T].runtimeClass.asInstanceOf[Class[T]], i)
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
		
		/** @since 0.1.0
		  * @deprecated For more complex use cases with less conflict, use [[get]] instead.
		  */
		@deprecated("Use get instead.", "da4a 0.2.0")
		def use [T: ClassTag]: CxtOption[T] = this.get
		/** @since 0.2.0 */
		infix def get [T] (clazz: Class[T]): CxtOption[T] = // TODO: tests
			given t: RequestItemClass = RequestItemClass(clazz)
			variablesWithOwner get thisClazz match
				case Some(varColl) =>
					//noinspection DuplicatedCode
					varColl get t.clazz match
						case Some(i) => Right(i.asInstanceOf[T])
						case None => Left(ContextNotGivenException())
				case None => Left(ContextNotGivenException())
		def get [T: ClassTag]: CxtOption[T] =
			this.get(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		/** @since 0.1.0 */
		infix def >> [T] (t: Class[T]): CxtOption[T] =
			this.get(t)
		
		/** @since 0.2.0 */
		infix def getOrNull [T] (clazz: Class[T]): T | Null = // TODO: tests
			this.get(clazz)
				.getOrElse(null)
		/** @since 0.2.0 */
		def getOrNull [T: ClassTag]: T| Null =
			this.getOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		/** @since 0.2.0 */
		infix def >?> [T] (t: Class[T]): T | Null = // TODO: tests
			this.getOrNull(t)
			
		/** @since 0.2.0 */
		@throws[ContextNotGivenException]
		infix def getUnsafe[T] (clazz: Class[T]): T = // TODO: docs
			this.get(clazz)
				.toTry.get
		/** @since 0.2.0 */
		@throws[ContextNotGivenException]
		def getUnsafe[T: ClassTag]: T = // TODO: docs
			this.getUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		/** @since 0.1.0 */
		@throws[ContextNotGivenException]
		infix def >!>[T] (t: Class[T]): T =
			this.getUnsafe(t)
		
		/** @since 0.2.0 */
		def use [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] = // TODO: tests
			this.get(clazz) match
				case Left(e) => ConsumeFailed[U](e)
				case Right(i) => ConsumeSucceed[U](consumer(i))
		/** @since 0.1.0 */
		infix def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			this.use[T,U](classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
		/** @since 0.1.0 */
		infix def >> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			this.use[T,U](consumer)
		/** @since 0.2.0 */
		def consume [T] (clazz: Class[T])(consumer: T => Any): ConsumeResult[Any] = // TODO: tests
			this.use[T,Any](clazz)(consumer)
		/** @since 0.1.0 */
		infix def consume [T: ClassTag] (consume: T => Any): ConsumeResult[Any] =
			this.use[T,Any](consume)
		/** @since 0.2.0 */
		def consuming [T] (clazz: Class[T])(jConsumer: JConsumer[T]): ConsumeResult[Unit] = // TODO: tests
			this.use[T,Unit](clazz)(jConsumer.asScala)
		
	}
	
	/** The result context of [[GivenContext.consuming]] operation.
	  *
	  * Gives a way to do the most useful following operation after the [[GivenContext.consuming]].
	  * Mostly depends on whether the consumer function executes successfully or not.
	  *
	  * Notice that once the consumer function is called, is is seen as succeed . The exception
	  * throws in the original consumer function will be just throws to the caller, not
	  * handled by this context.
	  *
	  * @tparam U The type of the return value that should returns by [[GivenContext.consuming]]'s
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
		
		/** The result of the consumer function in [[Either]] format.
		  * 
		  * If the consumer fails to execute for any reason (mostly the required context variable
		  * is not exists), this function will returns [[Left]] of the [[ContextNotGivenException]].
		  * The consumer throws exception will just be throws to the caller, not handled by this
		  * method.
		  * 
		  * @return [[Right]] of the consumer function returns value, or [[Left]] of the exceptions
		  *         while try to run the consumer function.
		  *         
		  * @since 0.2.0
		  */
		def toEither: Either[ContextNotGivenException, U] // TODO: docs and tests
		
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
		
		/** Get the result of this consumer function, or [[Null]] if the consumer cannot execute
		  * in some reason (most likely when the required context variable is not exists).
		  *
		  * This is relatively equals to `this.toOption.orNull`, or `this.orElse(null)`.
		  *
		  * @return The consumer function returns value itself, or `null` value.
		  *
		  * @since 0.2.0
		  */
		def orNull: U | Null = orElse(null)
		
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
	private class ConsumeSucceed[U] (succeedValue: U) extends ConsumeResult[U]:
		private def get: U = succeedValue
		override def toOption: Some[U] = Some(get)
		override def toEither: Either[ContextNotGivenException, U] = Right(get)
		override def orElse[P] (processor: =>P): U|P = get
		@throws[ContextNotGivenException]
		def ensureSuccess: U = get
	private class ConsumeFailed[U] (e: ContextNotGivenException) extends ConsumeResult[U]:
		override def toOption: None.type = None
		override def toEither: Either[ContextNotGivenException, U] = Left(e)
		override def orElse[P] (processor: =>P): U|P = processor
		@throws[ContextNotGivenException]
		def ensureSuccess: U = throw e
	
}
