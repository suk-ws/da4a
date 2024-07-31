package cc.sukazyo.std

import java.io.{PrintWriter, StringWriter}

package object throwable {
	
	@deprecated("Use Exceptions.ThrowableExtension instead", "da4a 0.2.0")
	implicit class ThrowableExtensions (throwable: Throwable) {
		
		/** Get the String content which [[Throwable.printStackTrace]] will print.
		  * @since 0.1.0
		  * @deprecated moved. Use [[Exceptions.ThrowableExtension.toLogString]] instead.
		  */
		@deprecated("Use Exceptions.ThrowableExtension.toLogString instead", "da4a 0.2.0")
		def toLogString: String =
			val stackTrace = StringWriter()
			throwable `printStackTrace` PrintWriter(stackTrace)
			stackTrace toString
		
	}
	
}
