package cc.sukazyo.std
package stacks

import scala.reflect.{classTag, ClassTag}
import scala.util.boundary
import scala.util.boundary.break

object WithCurrentStack {
	
	/** Get the current stack trace, and drop the first n nearest stack depends on the offset
	  * value given.
	  *
	  * Defaults the offset is 0, indicating the stack from the caller of this method. For
	  * example, if the call stack is like { main -> a -> b -> c }, and this method is called
	  * in function c(), then the returned stack will be { c, b, a, main }. If the offset is
	  * set to 1, then the returned stack will be { b, a, main }.
	  */
	@noinline
	def getStackTrace (offset: Int = 0): Array[StackTraceElement] = {
		val _offset = offset + 2
		val origins = Thread.currentThread.asInstanceOf[Thread].getStackTrace.asInstanceOf[Array[StackTraceElement]]
		origins.drop(_offset)
	}
	
	def getStackHeadBeforeClass[T: ClassTag]: StackTraceElement = {
		boundary {
			for (stack <- getStackTrace(1)) {
				if (!stack.getClassName.asInstanceOf[String].startsWith(classTag[T].runtimeClass.getName))
					break(stack)
			}
			StackTraceElement("unknown", "unknown", "unknown", -1)
		}
	}
	
}
