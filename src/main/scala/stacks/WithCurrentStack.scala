package cc.sukazyo.std
package stacks

import scala.reflect.{classTag, ClassTag}
import scala.util.boundary
import scala.util.boundary.break

trait WithCurrentStack {}

object WithCurrentStack {
	
	/** Indicates that current stack becomes empty after processed.
	  *
	  * @since 0.1.0
	  */
	class EmptyStackException extends RuntimeException
	
	/** Get the current stack trace, and drop the first n nearest stack depends on the offset
	  * value given.
	  *
	  * Defaults the offset is 0, indicating the stack from the caller of this method. For
	  * example, if the call stack is like { main -> a -> b -> c }, and this method is called
	  * in function c(), then the returned stack will be { c, b, a, main }. If the offset is
	  * set to 1, then the returned stack will be { b, a, main }.
	  *
	  * @throws SecurityException if a security manager exists and its checkPermission method
	  *                           doesn't allow getting the stack trace of current thread.
	  *
	  * @since 0.1.0
	  */
	@noinline
	@throws[SecurityException]
	def getStackTrace (offset: Int = 0): Array[StackTraceElement] = {
		Thread.currentThread.asInstanceOf[Thread].getStackTrace.asInstanceOf[Array[StackTraceElement]]
			.drop(1)
			.dropWhile(s => s.getClassName == classOf[WithCurrentStack].getName || s.getClassName == classOf[WithCurrentStack.type].getName)
			.drop(offset)
	}
	
	/** Get the current stack trace, and drop the first n nearest stack depends on the offset
	  * value given.
	  *
	  * Defaults the offset is 0, indicating the stack from the caller of this method. For
	  * example, if the call stack is like { main -> a -> b -> c }, and this method is called
	  * in function c(), then the returned stack will be { c, b, a, main }. If the offset is
	  * set to 1, then the returned stack will be { b, a, main }.
	  *
	  * @throws SecurityException if a security manager exists and its checkPermission method
	  *                           doesn't allow getting the stack trace of current thread.
	  *
	  * @see [[getStackTrace(Int)]]
	  *
	  * @since 0.2.0
	  */
	@noinline
	@throws[SecurityException]
	def getStackTrace: Array[StackTraceElement] =
		getStackTrace(0)
	
	/**
	  * Get the last [[StackTraceElement]] in current stack traces that is not in the given
	  * class.
	  *
	  * For example, if the calling stack trace is like `{ T.a() -> T.x() -> U.abc() -> U.x() }`,
	  * then if call this method with U, the returned stack element will be `T.x()`. But if the
	  * current stack trace is like `{ T.a() -> U.m() -> T.x() }`, then call this method with
	  * class U, the returned stack element will be `T.x()`.
	  *
	  * @tparam T The class that stack trace element belong to this class will be ignored.
	  * @throws SecurityException if a security manager exists and its checkPermission method
	  *                           doesn't allow getting the stack trace of current thread.
	  * @throws EmptyStackException if there's no any stack trace element in current stack trace
	  *                             that is not in the given class. This may happens when this
	  *                             thread is just called in the main method etc.
	  * @return The last(most recent) stack trace element that is not in the given class
	  */
	@throws[SecurityException]
	@throws[EmptyStackException]
	def getStackHeadBeforeClass[T: ClassTag]: StackTraceElement = {
		getStackHeadBeforeClass(classTag[T].runtimeClass)
	}
	
	/**
	  * Get the last [[StackTraceElement]] in current stack traces that is not in the given
	  * class.
	  *
	  * For example, if the calling stack trace is like `{ T.a() -> T.x() -> U.abc() -> U.x() }`,
	  * then if call this method with U, the returned stack element will be `T.x()`. But if the
	  * current stack trace is like `{ T.a() -> U.m() -> T.x() }`, then call this method with
	  * class U, the returned stack element will be `T.x()`.
	  *
	  * @param clazz The class that stack trace element belong to this class will be ignored.
	  * @throws SecurityException   if a security manager exists and its checkPermission method
	  *                             doesn't allow getting the stack trace of current thread.
	  * @throws EmptyStackException if there's no any stack trace element in current stack trace
	  *                             that is not in the given class. This may happens when this
	  *                             thread is just called in the main method etc.
	  * @return The last(most recent) stack trace element that is not in the given class
	  */
	@throws[SecurityException]
	@throws[EmptyStackException]
	def getStackHeadBeforeClass (clazz: Class[?]): StackTraceElement = {
		boundary {
			for (stack <- getStackTrace) {
				if (!stack.getClassName.asInstanceOf[String].startsWith(clazz.getName))
					break(stack)
			}
			throw EmptyStackException()
		}
	}
	
}
