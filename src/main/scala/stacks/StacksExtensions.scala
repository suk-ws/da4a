package cc.sukazyo.std
package stacks

import stacks.WithCurrentStack.EmptyStackException

import scala.reflect.{classTag, ClassTag}

object StacksExtensions {
	
	implicit class StacksExtensions (stacks: Array[StackTraceElement]) {
		
		/** todo docs
		  *
		  * @since 0.2.0
		  */
		@throws[SecurityException | EmptyStackException]
		def dropWhileClass (className: String): Array[StackTraceElement] = {
			stacks.dropWhile { stack => stack.getClassName == className }
		}
		
		/** todo docs
		  *
		  * @since 0.2.0
		  */
		@throws[SecurityException | EmptyStackException]
		def dropWhileClass (clazz: Class[?]): Array[StackTraceElement] = {
			stacks.dropWhileClass(clazz.getName.nn)
		}
		
		/** todo docs
		  * 
		  * @since 0.2.0
		  */
		@throws[SecurityException | EmptyStackException]
		def dropWhileClass [T: ClassTag]: Array[StackTraceElement] =
			this.dropWhileClass(classTag[T].runtimeClass)
		
	}
	
}
