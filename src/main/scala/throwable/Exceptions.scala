package cc.sukazyo.std
package throwable

import java.io.{PrintWriter, StringWriter}
import scala.collection.mutable.ListBuffer

object Exceptions {
	
	/** Get the String content which [[Throwable.printStackTrace]] will print.
	  * @since 0.2.0
	  */
	def printString (self: Throwable): String =
		val stackTrace = StringWriter()
		self.printStackTrace(PrintWriter(stackTrace))
		stackTrace.toString
	
	/** Get the exception information and its caused exceptions in String format.
	  *
	  * This will print the [[Throwable.toString]] message (which contains the exception's class
	  * and the localized message), and if there are some [[Throwable.getCause caused]]
	  * [[Throwable]]s, the caused exceptions will be print in the following lines prefixed by
	  * the ` - Caused by: `. There are no empty line at last.
	  *
	  * The final output will be like:
	  * {{{
	  *     com.pengrad.telegrambot.TelegramException: java.net.SocketTimeoutException: timeout
	  *      - Caused by: java.net.SocketTimeoutException: timeout
	  * }}}
	  *
	  *  @since 0.2.0
	  */
	def printStringSimple (self: Throwable): String =
		val outs = ListBuffer.empty[String]
		outs += self.toString
		var caused = self.getCause
		while (caused != null)
			outs += s" - Caused by: ${caused.toString}"
			caused = caused.getCause
		outs.mkString("\n")
	
	implicit class ThrowableExtension (self: Throwable) {
		
		/** Get the String content which [[Throwable.printStackTrace]] will print.
		  * @since 0.2.0
		  */
		def toLogString: String = Exceptions.printString(self)
		
		/** Get the String content which [[Throwable.printStackTrace]] will print.
		  * @since 0.2.0
		  */
		def printString: String =
			self.toLogString
		
		/** Get the exception information and its caused exceptions in String format.
		  *
		  * This will print the [[Throwable.toString]] message (which contains the exception's class
		  * and the localized message), and if there are some [[Throwable.getCause caused]]
		  * [[Throwable]]s, the caused exceptions will be print in the following lines prefixed by
		  * the ` - Caused by: `. There are no empty line at last.
		  *
		  * The final output will be like:
		  * {{{
		  *     com.pengrad.telegrambot.TelegramException: java.net.SocketTimeoutException: timeout
		  *      - Caused by: java.net.SocketTimeoutException: timeout
		  * }}}
		  *
		  * @since 0.2.0
		  */
		def printStringSimple: String =
			Exceptions.printStringSimple(self)
		
	}
	
}
