package cc.sukazyo.std
package stacks

import scala.reflect.{classTag, ClassTag}

object StacksExtensions {
	
	/** todo docs
	  *
	  * @since 0.2.0
	  */
	@throws[SecurityException]
	def dropWhileClass (className: String)(stacks: Array[StackTraceElement]): Array[StackTraceElement] = {
		stacks.dropWhile { stack => stack.getClassName == className }
	}
	
	/** todo docs
	  *
	  * @since 0.2.0
	  */
	@throws[SecurityException]
	def dropWhileClass (clazz: Class[?])(stacks: Array[StackTraceElement]): Array[StackTraceElement] = {
		dropWhileClass(clazz.getName.asInstanceOf[String])(stacks)
	}
	
	/** todo docs
	  *
	  * @since 0.2.0
	  */
	@throws[SecurityException]
	def dropWhileClass[T: ClassTag] (stacks: Array[StackTraceElement]): Array[StackTraceElement] =
		dropWhileClass(classTag[T].runtimeClass)(stacks)
	
	implicit class StackTrackArrayExt (stacks: Array[StackTraceElement]) {
		
		/** todo docs
		  *
		  * @since 0.2.0
		  */
		@throws[SecurityException]
		def dropWhileClass (className: String): Array[StackTraceElement] =
			StacksExtensions.dropWhileClass(className)(stacks)
		
		/** todo docs
		  *
		  * @since 0.2.0
		  */
		@throws[SecurityException]
		def dropWhileClass (clazz: Class[?]): Array[StackTraceElement] =
			StacksExtensions.dropWhileClass(clazz)(stacks)
		
		/** todo docs
		  * 
		  * @since 0.2.0
		  */
		@throws[SecurityException]
		def dropWhileClass [T: ClassTag]: Array[StackTraceElement] =
			StacksExtensions.dropWhileClass[T](stacks)
		
	}
	
}
