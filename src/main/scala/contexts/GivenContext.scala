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
) extends AbstractGivenContext with mutable.Cloneable[GivenContext] {
	
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
	
	override def clone (): GivenContext =
		new GivenContext(
			variables.map(_ -> _),
			variablesWithOwner.map(_ -> _.map(_ -> _))
		)
	
	def sizeGlobal: Int = variables.size
	def isEmptyGlobal: Boolean = variables.isEmpty
	def nonEmptyGlobal: Boolean = variables.nonEmpty
	
	def size: Int =
		sizeGlobal + ownedScopes.map(_.size).sum
	def isEmpty: Boolean =
		isEmptyGlobal && ownedScopes.forall(_.isEmpty)
	def nonEmpty: Boolean =
		nonEmptyGlobal || ownedScopes.exists(_.nonEmpty)
	
	def ownedScopes: List[OwnedContext] =
		variablesWithOwner.map((k, v) => new OwnedContext(k)).toList
	
	infix def provide [T] (clazz: Class[T], i: T): Unit =
		variables += (clazz -> i)
	infix def provide [T: ClassTag] (i: T): Unit =
		this.provide(classTag[T].runtimeClass.asInstanceOf[Class[T]], i)
	infix def << [T: ClassTag] (is: (Class[T], T)): Unit =
		val (_, i) = is
		this.provide[T](i)
	infix def << [T: ClassTag] (i: T): Unit =
		this.provide[T](i)
	
	//noinspection ScalaDeprecation
	def use [T: ClassTag]: CxtOption[T] = this.get
	infix def get [T] (clazz: Class[T]): CxtOption[T] =
		given t: RequestItemClass = RequestItemClass(clazz)
		//noinspection DuplicatedCode
		variables get t.clazz match
			case Some(i) => Right(i.asInstanceOf[T])
			case None => Left(ContextNotGivenException())
	def get [T: ClassTag]: CxtOption[T] =
		this.get(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	infix def >> [T] (t: Class[T]): CxtOption[T] =
		this.get(t)
	
	infix def getOrNull [T] (clazz: Class[T]): T | Null =
		this.get(clazz)
			.getOrElse(null)
	def getOrNull [T: ClassTag]: T | Null =
		this.getOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	infix def >?> [T] (t: Class[T]): T | Null =
		this.getOrNull(t)
	
	@throws[ContextNotGivenException]
	infix def getUnsafe [T] (clazz: Class[T]): T =
		this.get(clazz)
			.toTry.get
	@throws[ContextNotGivenException]
	def getUnsafe [T: ClassTag]: T =
		this.getUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	@throws[ContextNotGivenException]
	infix def >!> [T] (t: Class[T]): T =
		this.getUnsafe(t)
	
	def discard [T] (clazz: Class[T]): Boolean = ???
	def discard [T: ClassTag]: Boolean = this.discard(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	
	def pop [T] (clazz: Class[T]): CxtOption[T] = ???
	def pop [T: ClassTag]: CxtOption[T] = this.pop(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	infix def !>> [T] (clazz: Class[T]): CxtOption[T] = pop(clazz)
	
	def popOrNull [T] (clazz: Class[T]): T | Null = ???
	def popOrNull [T: ClassTag]: T | Null = this.popOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	infix def !>?> [T] (clazz: Class[T]): T | Null = this.popOrNull(clazz)
	
	@throws[ContextNotGivenException]
	def popUnsafe [T] (clazz: Class[T]): T = ???
	@throws[ContextNotGivenException]
	def popUnsafe [T: ClassTag]: T = this.popUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	@throws[ContextNotGivenException]
	infix def !>!> [T] (clazz: Class[T]): T = this.popUnsafe(clazz)
	
	def use [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] =
		this.get[T](clazz) match
			case Left(e) => ConsumeFailed[U](e)
			case Right(i) => ConsumeSucceed[U](consumer(i))
	infix def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T,U](classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
	infix def >> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T,U](consumer)
	def consume [T] (clazz: Class[T])(consumer: T => Any): ConsumeResult[Any] =
		this.use[T,Any](clazz)(consumer)
	infix def consume [T: ClassTag] (consumer: T => Any): ConsumeResult[Any] =
		this.use[T,Any](consumer)
	def consuming [T] (clazz: Class[T])(jConsumer: JConsumer[T]): ConsumeResult[Unit] =
		this.use[T,Unit](clazz)(jConsumer.asScala)
	
	def take [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] = ???
	def take [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] = this.take(classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
	infix def !>> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] = this.take(consumer)
	
	def / (owner: Class[?]): OwnedContext =
		OwnedContext(owner)
	def / (owner: AnyRef): OwnedContext =
		OwnedContext(owner.getClass)
	def ownedBy [O: ClassTag]: OwnedContext =
		OwnedContext(classTag[O].runtimeClass)
	def ownedBy [O] (clazz: Class[O]): OwnedContext =
		OwnedContext(clazz)
	
	/** An access helper for an owned context in the [[GivenContext]].
	  *
	  * This connects to an owned context in the [[GivenContext]] and provides access to it.
	  *
	  * Each owned context is separated to another owned context, and also separated to the global
	  * context. So that you can register a new context variables here without worrying about if
	  * it may conflict to the other's registered context variables with the same type, as long
	  * as you set a unique [[Class]] as the owned context's owner (aka. key).
	  *
	  * @param thisClazz the key of the owned context that this helper connects to. Also, this
	  *                  is the class which owns the owned context.
	  *
	  *                  since 0.1.0
	  *
	  * @since 0.1.0
	  */
	class OwnedContext (thisClazz: Class[?]) extends AbstractGivenContextOwnedContext {
		private given folderClass: FolderClass = FolderClass(Some(thisClazz))
		
		private def getThisMap: Option[ImplicitsMap[Any]] =
			variablesWithOwner get thisClazz
		private def getThisMapOrCreate: ImplicitsMap[Any] =
			variablesWithOwner.getOrElseUpdate(
				thisClazz, mutable.HashMap.empty)
		
		def isOwnedBy [OT: ClassTag]: Boolean =
			isOwnedBy(classTag[OT].runtimeClass)
		def isOwnedBy (clazz: Class[?]): Boolean =
			thisClazz == clazz
		
		def size: Int = getThisMap.map(_.size).getOrElse(0)
		def isEmpty: Boolean = getThisMap.forall(_.isEmpty)
		def nonEmpty: Boolean = getThisMap.exists(_.nonEmpty)
		
		infix def provide [T] (clazz: Class[T], i: T): Unit =
			getThisMapOrCreate.addOne(clazz -> i)
		infix def provide [T: ClassTag] (i: T): Unit =
			this.provide(classTag[T].runtimeClass.asInstanceOf[Class[T]], i)
		infix def << [T: ClassTag] (is: (Class[T], T)): Unit =
			val (_, i) = is
			this.provide[T](i)
		infix def << [T: ClassTag] (i: T): Unit =
			this.provide[T](i)
		
		//noinspection ScalaDeprecation
		override def use [T: ClassTag]: CxtOption[T] = this.get
		infix def get [T] (clazz: Class[T]): CxtOption[T] =
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
		infix def >> [T] (t: Class[T]): CxtOption[T] =
			this.get(t)
		
		infix def getOrNull [T] (clazz: Class[T]): T | Null =
			this.get(clazz)
				.getOrElse(null)
		def getOrNull [T: ClassTag]: T| Null =
			this.getOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		infix def >?> [T] (t: Class[T]): T | Null =
			this.getOrNull(t)
			
		@throws[ContextNotGivenException]
		infix def getUnsafe[T] (clazz: Class[T]): T =
			this.get(clazz)
				.toTry.get
		@throws[ContextNotGivenException]
		def getUnsafe[T: ClassTag]: T =
			this.getUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		@throws[ContextNotGivenException]
		infix def >!>[T] (t: Class[T]): T =
			this.getUnsafe(t)
		
		def use [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] =
		def discard[T] (clazz: Class[T]): Boolean = ???
		def discard[T: ClassTag]: Boolean = this.discard(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		
		def pop[T] (clazz: Class[T]): CxtOption[T] = ???
		def pop[T: ClassTag]: CxtOption[T] = this.pop(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		infix def !>>[T] (clazz: Class[T]): CxtOption[T] = pop(clazz)
		
		def popOrNull[T] (clazz: Class[T]): T | Null = ???
		def popOrNull[T: ClassTag]: T | Null = this.popOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		infix def !>?>[T] (clazz: Class[T]): T | Null = this.popOrNull(clazz)
		
		@throws[ContextNotGivenException]
		def popUnsafe[T] (clazz: Class[T]): T = ???
		@throws[ContextNotGivenException]
		def popUnsafe[T: ClassTag]: T = this.popUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		@throws[ContextNotGivenException]
		infix def !>!>[T] (clazz: Class[T]): T = this.popUnsafe(clazz)
		
		def use [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] = // TODO: tests
			this.get(clazz) match
				case Left(e) => ConsumeFailed[U](e)
				case Right(i) => ConsumeSucceed[U](consumer(i))
		infix def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			this.use[T,U](classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
		infix def >> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			this.use[T,U](consumer)
		def consume [T] (clazz: Class[T])(consumer: T => Any): ConsumeResult[Any] =
			this.use[T,Any](clazz)(consumer)
		infix def consume [T: ClassTag] (consume: T => Any): ConsumeResult[Any] =
			this.use[T,Any](consume)
		def consuming [T] (clazz: Class[T])(jConsumer: JConsumer[T]): ConsumeResult[Unit] =
			this.use[T,Unit](clazz)(jConsumer.asScala)
		
		def take[T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] = ???
		def take[T: ClassTag, U] (consumer: T => U): ConsumeResult[U] = this.take(classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
		infix def !>>[T: ClassTag, U] (consumer: T => U): ConsumeResult[U] = this.take(consumer)
		
	}
	
	/** The result context of [[GivenContext.consuming]] operation.
	  *
	  * Gives a way to do the most useful following operation after the [[GivenContext.consuming]].
	  * Mostly depends on whether the consumer function executes successfully or not.
	  *
	  * Notice that once the consumer function is called, is seen as succeed . The exception
	  * throws in the original consumer function will be just throws to the caller, not
	  * handled by this context.
	  *
	  * @tparam U The type of the return value that should returns by [[GivenContext.consuming]]'s
	  *           consumer function.
	  *
	  * @since 0.1.0
	  */
	trait ConsumeResult[U] extends AbstractConsumeResult[U] {
		def toOption: Option[U]
		def toEither: Either[ContextNotGivenException, U]
		def |? : Option[U] = toOption
		def orElse[P] (processor: => P): U | P
		def ||[P] (processor: => P): U | P = orElse(processor)
		def orNull: U | Null = orElse(null)
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
