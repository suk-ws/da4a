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
	
	override def sizeGlobal: Int = variables.size
	override def isEmptyGlobal: Boolean = variables.isEmpty
	override def nonEmptyGlobal: Boolean = variables.nonEmpty
	
	override def size: Int =
		sizeGlobal + ownedScopes.map(_.size).sum
	override def isEmpty: Boolean =
		isEmptyGlobal && ownedScopes.forall(_.isEmpty)
	override def nonEmpty: Boolean =
		nonEmptyGlobal || ownedScopes.exists(_.nonEmpty)
	
	override def ownedScopes: List[OwnedContext] =
		variablesWithOwner.map((k, v) => new OwnedContext(k)).toList
	
	override infix def provide [T] (clazz: Class[T], i: T): Unit =
		variables += (clazz -> i)
	override infix def provide [T: ClassTag] (i: T): Unit =
		this.provide(classTag[T].runtimeClass.asInstanceOf[Class[T]], i)
	override infix def << [T: ClassTag] (is: (Class[T], T)): Unit =
		val (_, i) = is
		this.provide[T](i)
	override infix def << [T: ClassTag] (i: T): Unit =
		this.provide[T](i)
	
	//noinspection ScalaDeprecation
	override def use [T: ClassTag]: CxtOption[T] = this.get
	override infix def get [T] (clazz: Class[T]): CxtOption[T] =
		given t: RequestItemClass = RequestItemClass(clazz)
		//noinspection DuplicatedCode
		variables get t.clazz match
			case Some(i) => Right(i.asInstanceOf[T])
			case None => Left(ContextNotGivenException())
	override def get [T: ClassTag]: CxtOption[T] =
		this.get(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	override infix def >> [T] (t: Class[T]): CxtOption[T] =
		this.get(t)
	
	override infix def getOrNull [T] (clazz: Class[T]): T | Null =
		this.get(clazz)
			.getOrElse(null)
	override def getOrNull [T: ClassTag]: T | Null =
		this.getOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	override infix def >?> [T] (t: Class[T]): T | Null =
		this.getOrNull(t)
	
	@throws[ContextNotGivenException]
	override infix def getUnsafe [T] (clazz: Class[T]): T =
		this.get(clazz)
			.toTry.get
	@throws[ContextNotGivenException]
	override def getUnsafe [T: ClassTag]: T =
		this.getUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	@throws[ContextNotGivenException]
	override infix def >!> [T] (t: Class[T]): T =
		this.getUnsafe(t)
	
	override def discard [T] (clazz: Class[T]): Boolean =
		this.pop(clazz).map(_=>true).getOrElse(false)
	override def discard [T: ClassTag]: Boolean =
		this.discard(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	
	override def pop [T] (clazz: Class[T]): CxtOption[T] =
		this.get(clazz).map { value => this.variables.remove(clazz); value }
	override def pop [T: ClassTag]: CxtOption[T] =
		this.pop(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	override infix def !>> [T] (clazz: Class[T]): CxtOption[T] =
		pop(clazz)
	
	override def popOrNull [T] (clazz: Class[T]): T | Null =
		this.pop(clazz).getOrElse(null)
	override def popOrNull [T: ClassTag]: T | Null =
		this.popOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	override infix def !>?> [T] (clazz: Class[T]): T | Null =
		this.popOrNull(clazz)
	
	@throws[ContextNotGivenException]
	override def popUnsafe [T] (clazz: Class[T]): T =
		this.pop(clazz).toTry.get
	@throws[ContextNotGivenException]
	override def popUnsafe [T: ClassTag]: T =
		this.popUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
	@throws[ContextNotGivenException]
	override infix def !>!> [T] (clazz: Class[T]): T =
		this.popUnsafe(clazz)
	
	override def use [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] =
		this.get[T](clazz) match
			case Left(e) => ConsumeFailed[U](e)
			case Right(i) => ConsumeSucceed[U](consumer(i))
	override infix def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T,U](classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
	override infix def >> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T,U](consumer)
	override def consume [T] (clazz: Class[T])(consumer: T => Any): ConsumeResult[Any] =
		this.use[T,Any](clazz)(consumer)
	override infix def consume [T: ClassTag] (consumer: T => Any): ConsumeResult[Any] =
		this.use[T,Any](consumer)
	override def consuming [T] (clazz: Class[T])(jConsumer: JConsumer[T]): ConsumeResult[Unit] =
		this.use[T,Unit](clazz)(jConsumer.asScala)
	
	override def take [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] =
		//noinspection DuplicatedCode
		this.pop(clazz) match
			case Left(e) => ConsumeFailed[U](e)
			case Right(i) => ConsumeSucceed[U](consumer(i))
	override def take [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.take(classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
	override infix def !>> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.take(consumer)
	
	override def has [T] (clazz: Class[T]): Boolean =
		this.get(clazz).isRight
	override def has [T: ClassTag]: Boolean =
		this.has(classTag[T].runtimeClass)
	override def has [T: ClassTag] (i: T): Boolean =
		this.has
	override infix def ?: [T] (clazz: Class[T]): Boolean =
		this.has(clazz)
	override infix def ?: [T: ClassTag] (i: T): Boolean =
		this.has(classTag[T].runtimeClass)
	
	override def accept [T] (clazz: Class[T]): Boolean =
		this.discard(clazz)
	override def accept [T: ClassTag]: Boolean =
		this.accept(classTag[T].runtimeClass)
	override def accept [T: ClassTag] (i: T): Boolean =
		this.accept
	override infix def ?^ [T] (clazz: Class[T]): Boolean =
		this.accept(clazz)
	override infix def ?^ [T: ClassTag] (i: T): Boolean =
		this.accept
	
	override def contains [T: ClassTag] (value: T): Boolean =
		this.get[T] == value
	override infix def ?* [T: ClassTag] (value: T): Boolean =
		this.contains(value)
	
	override def drain [T: ClassTag] (value: T): Boolean =
		if this.contains(value) then
			this.discard[T]
			true
		else false
	override infix def ?! [T: ClassTag] (value: T): Boolean =
		this.drain(value)
	
	override def / (owner: Class[?]): OwnedContext =
		OwnedContext(owner)
	override def / (owner: AnyRef): OwnedContext =
		OwnedContext(owner.getClass)
	override def ownedBy [O: ClassTag]: OwnedContext =
		OwnedContext(classTag[O].runtimeClass)
	override def ownedBy [O] (clazz: Class[O]): OwnedContext =
		OwnedContext(clazz)
	
	class OwnedContext (thisClazz: Class[?]) extends AbstractGivenContextOwnedContext {
		private given folderClass: FolderClass = FolderClass(Some(thisClazz))
		
		private def getThisMap: Option[ImplicitsMap[Any]] =
			variablesWithOwner get thisClazz
		private def getThisMapOrCreate: ImplicitsMap[Any] =
			variablesWithOwner.getOrElseUpdate(
				thisClazz, mutable.HashMap.empty)
		
		override def isOwnedBy [OT: ClassTag]: Boolean =
			isOwnedBy(classTag[OT].runtimeClass)
		override def isOwnedBy (clazz: Class[?]): Boolean =
			thisClazz == clazz
		
		override def size: Int = getThisMap.map(_.size).getOrElse(0)
		override def isEmpty: Boolean = getThisMap.forall(_.isEmpty)
		override def nonEmpty: Boolean = getThisMap.exists(_.nonEmpty)
		
		override infix def provide [T] (clazz: Class[T], i: T): Unit =
			getThisMapOrCreate.addOne(clazz -> i)
		override infix def provide [T: ClassTag] (i: T): Unit =
			this.provide(classTag[T].runtimeClass.asInstanceOf[Class[T]], i)
		override infix def << [T: ClassTag] (is: (Class[T], T)): Unit =
			val (_, i) = is
			this.provide[T](i)
		override infix def << [T: ClassTag] (i: T): Unit =
			this.provide[T](i)
		
		//noinspection ScalaDeprecation
		override def use [T: ClassTag]: CxtOption[T] = this.get
		override infix def get [T] (clazz: Class[T]): CxtOption[T] =
			given t: RequestItemClass = RequestItemClass(clazz)
			variablesWithOwner get thisClazz match
				case Some(varColl) =>
					//noinspection DuplicatedCode
					varColl get t.clazz match
						case Some(i) => Right(i.asInstanceOf[T])
						case None => Left(ContextNotGivenException())
				case None => Left(ContextNotGivenException())
		override def get [T: ClassTag]: CxtOption[T] =
			this.get(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		override infix def >> [T] (t: Class[T]): CxtOption[T] =
			this.get(t)
		
		override infix def getOrNull [T] (clazz: Class[T]): T | Null =
			this.get(clazz)
				.getOrElse(null)
		override def getOrNull [T: ClassTag]: T| Null =
			this.getOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		override infix def >?> [T] (t: Class[T]): T | Null =
			this.getOrNull(t)
			
		@throws[ContextNotGivenException]
		override infix def getUnsafe[T] (clazz: Class[T]): T =
			this.get(clazz)
				.toTry.get
		@throws[ContextNotGivenException]
		override def getUnsafe[T: ClassTag]: T =
			this.getUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		@throws[ContextNotGivenException]
		override infix def >!>[T] (t: Class[T]): T =
			this.getUnsafe(t)
		
		override def discard[T] (clazz: Class[T]): Boolean =
			this.pop(clazz).map(_ => true).getOrElse(false)
		override def discard[T: ClassTag]: Boolean =
			this.discard(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		
		override def pop[T] (clazz: Class[T]): CxtOption[T] =
			this.get(clazz).map { value =>
				variablesWithOwner.get(thisClazz) match {
					case None =>
					case Some(varColl) =>
						varColl.remove(clazz)
				}
				value
			}
		override def pop[T: ClassTag]: CxtOption[T] = this.pop(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		override infix def !>>[T] (clazz: Class[T]): CxtOption[T] = pop(clazz)
		
		override def popOrNull[T] (clazz: Class[T]): T | Null =
			this.pop(clazz).getOrElse(null)
		override def popOrNull[T: ClassTag]: T | Null = this.popOrNull(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		override infix def !>?>[T] (clazz: Class[T]): T | Null = this.popOrNull(clazz)
		
		@throws[ContextNotGivenException]
		override def popUnsafe[T] (clazz: Class[T]): T =
			this.pop(clazz).toTry.get
		@throws[ContextNotGivenException]
		override def popUnsafe[T: ClassTag]: T = this.popUnsafe(classTag[T].runtimeClass.asInstanceOf[Class[T]])
		@throws[ContextNotGivenException]
		override infix def !>!>[T] (clazz: Class[T]): T = this.popUnsafe(clazz)
		
		override def use [T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] =
			this.get(clazz) match
				case Left(e) => ConsumeFailed[U](e)
				case Right(i) => ConsumeSucceed[U](consumer(i))
		override infix def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			this.use[T,U](classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
		override infix def >> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			this.use[T,U](consumer)
		override def consume [T] (clazz: Class[T])(consumer: T => Any): ConsumeResult[Any] =
			this.use[T,Any](clazz)(consumer)
		override infix def consume [T: ClassTag] (consume: T => Any): ConsumeResult[Any] =
			this.use[T,Any](consume)
		override def consuming [T] (clazz: Class[T])(jConsumer: JConsumer[T]): ConsumeResult[Unit] =
			this.use[T,Unit](clazz)(jConsumer.asScala)
		
		override def take[T, U] (clazz: Class[T])(consumer: T => U): ConsumeResult[U] =
			//noinspection DuplicatedCode
			this.pop(clazz) match
				case Left(e) => ConsumeFailed[U](e)
				case Right(i) => ConsumeSucceed[U](consumer(i))
		override def take[T: ClassTag, U] (consumer: T => U): ConsumeResult[U] = this.take(classTag[T].runtimeClass.asInstanceOf[Class[T]])(consumer)
		override infix def !>>[T: ClassTag, U] (consumer: T => U): ConsumeResult[U] = this.take(consumer)
		
		
		override def has [T] (clazz: Class[T]): Boolean =
			this.get(clazz).isRight
		override def has [T: ClassTag]: Boolean =
			this.has(classTag[T].runtimeClass)
		override def has [T: ClassTag] (i: T): Boolean =
			this.has
		override infix def ?: [T] (clazz: Class[T]): Boolean =
			this.has(clazz)
		override infix def ?: [T: ClassTag] (i: T): Boolean =
			this.has(classTag[T].runtimeClass)
		
		override def accept [T] (clazz: Class[T]): Boolean =
			this.discard(clazz)
		override def accept [T: ClassTag]: Boolean =
			this.accept(classTag[T].runtimeClass)
		override def accept [T: ClassTag] (i: T): Boolean =
			this.accept
		override infix def ?^ [T] (clazz: Class[T]): Boolean =
			this.accept(clazz)
		override infix def ?^ [T: ClassTag] (i: T): Boolean =
			this.accept
		
		override def contains [T: ClassTag] (value: T): Boolean =
			this.get[T] == value
		override infix def ?* [T: ClassTag] (value: T): Boolean =
			this.contains(value)
		
		override def drain [T: ClassTag] (value: T): Boolean =
			if this.contains(value) then
				this.discard[T]
				true
			else false
		override infix def ?! [T: ClassTag] (value: T): Boolean =
			this.drain(value)
		
	}
	
	trait ConsumeResult[U] extends AbstractConsumeResult[U] {
		override def toOption: Option[U]
		override def toEither: Either[ContextNotGivenException, U]
		override def |? : Option[U] = toOption
		override def orElse[P] (processor: => P): U | P
		override def ||[P] (processor: => P): U | P = orElse(processor)
		override def orNull: U | Null = orElse(null)
		@throws[ContextNotGivenException]
		override def ensureSuccess: U
	}
	private class ConsumeSucceed[U] (succeedValue: U) extends ConsumeResult[U]:
		private def get: U = succeedValue
		override def toOption: Some[U] = Some(get)
		override def toEither: Either[ContextNotGivenException, U] = Right(get)
		override def orElse[P] (processor: =>P): U|P = get
		@throws[ContextNotGivenException]
		override def ensureSuccess: U = get
	private class ConsumeFailed[U] (e: ContextNotGivenException) extends ConsumeResult[U]:
		override def toOption: None.type = None
		override def toEither: Either[ContextNotGivenException, U] = Left(e)
		override def orElse[P] (processor: =>P): U|P = processor
		@throws[ContextNotGivenException]
		override def ensureSuccess: U = throw e
	
}
