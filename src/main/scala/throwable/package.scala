package cc.sukazyo.std

import java.io.{PrintWriter, StringWriter}

package object throwable {
	
	implicit class ThrowableExtensions (throwable: Throwable) {
		
		/** Get the String content which [[Throwable.printStackTrace]] will print. */
		def toLogString: String =
			val stackTrace = StringWriter()
			throwable `printStackTrace` PrintWriter(stackTrace)
			stackTrace toString
		
	}
	
}
