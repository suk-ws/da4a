package cc.sukazyo.std
package throwable

import java.io.{PrintWriter, StringWriter}

implicit class ThrowableExtensions (throwable: Throwable) {
	
	/** Get the String content which [[Throwable.printStackTrace]] will print. */
	def toLogString: String =
		val stackTrace = StringWriter()
		throwable `printStackTrace` PrintWriter(stackTrace)
		stackTrace toString
	
}
