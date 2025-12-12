package cc.sukazyo.std
package command

import text.CharStringify.CharStringifyOps

import scala.collection.mutable.ArrayBuffer
import scala.util.boundary
import scala.util.boundary.break

object InputCommandParser {
	
	private def classicBaseBuilder (): Builder = Builder()
		.setNewLineAsSeparator(false)
		.setNewLineBreakQuotes(false)
		.setTabAsSeparator(false)
	
	val classicLossyParser: InputCommandParser = classicBaseBuilder().build()
	
	val classicStrictParser: InputCommandParser = classicBaseBuilder()
		.setAllowQuoteInQuotes(false)
		.setAllowUnclosedQuotes(false)
		.setAllowNewLine(false)
		.setAllowNewLineInQuotes(false)
		.setAllowEscapeAtEnd(false)
		.build()
	
	class Builder {
		
		private var allowQuoteInQuotes: Boolean = true
		def setAllowQuoteInQuotes (value: Boolean): this.type = { this.allowQuoteInQuotes = value; this }
		private var allowUnclosedQuotes: Boolean = true
		def setAllowUnclosedQuotes (value: Boolean): this.type = { this.allowUnclosedQuotes = value; this }
		private var allowNewLine: Boolean = true
		def setAllowNewLine (value: Boolean): this.type = { this.allowNewLine = value; this }
		private var newLineAsSeparator: Boolean = true
		def setNewLineAsSeparator (value: Boolean): this.type = { this.newLineAsSeparator = value; this }
		private var allowNewLineInQuotes: Boolean = true
		def setAllowNewLineInQuotes (value: Boolean): this.type = { this.allowNewLineInQuotes = value; this }
		private var newLineBreakQuotes: Boolean = true
		def setNewLineBreakQuotes (value: Boolean): this.type = { this.newLineBreakQuotes = value; this }
		private var tabAsSeparator: Boolean = true
		def setTabAsSeparator (value: Boolean): this.type = { this.tabAsSeparator = value; this }
		private var allowEscapeAtEnd: Boolean = true
		def setAllowEscapeAtEnd (value: Boolean): this.type = { this.allowEscapeAtEnd = value; this }
		private var escapeNonSpecialChars: Boolean = false
		def setEscapeNonSpecialChars (value: Boolean): this.type = { this.escapeNonSpecialChars = value; this }
		
		def build (): InputCommandParser = {
			val self = this
			new InputCommandParser {
				override val allowQuoteInQuotes: Boolean = self.allowQuoteInQuotes
				override val allowUnclosedQuotes: Boolean = self.allowUnclosedQuotes
				override val allowNewLine: Boolean = self.allowNewLine
				override val newLineAsSeparator: Boolean = self.newLineAsSeparator
				override val allowNewLineInQuotes: Boolean = self.allowNewLineInQuotes
				override val newLineBreakQuotes: Boolean = self.newLineBreakQuotes
				override val tabAsSeparator: Boolean = self.tabAsSeparator
				override val allowEscapeAtEnd: Boolean = self.allowEscapeAtEnd
				override val escapeNonSpecialChars: Boolean = self.escapeNonSpecialChars
			}
		}
	}
	
	val default: InputCommandParser = Builder().build()
	
}

trait InputCommandParser {

//	/** Allow quotes to quote a segment of text as one argument, no matter the special chars
//	  * in it. If set false, quotes will be treated as normal text characters. */
//	val useQuotes: Boolean
//	/** A quoted text must be a whole argument, disallow mixes of quoted text and non-quoted
//	  * text in one argument (like `text" that with spaces"~~~`) */
//	val strictQuoteFullArgument: Boolean
	/** Allow the unmatched quotes exists in quoted area (like ' in ""). */
	val allowQuoteInQuotes: Boolean
	/** Allow a quote to be unclosed. If set true, texts from the quote starts till the end of
	  * text will be parsed as one argument. */
	val allowUnclosedQuotes: Boolean
	/** Allow the new line (\n) to be existed outside the quotes. */
	val allowNewLine: Boolean
	/** Make the new line as an argument separator like space char. */
	val newLineAsSeparator: Boolean
	/** Allow a new line in quotes. */
	val allowNewLineInQuotes: Boolean
	/** Makes the new line in quotes immediately end the quote and separate the argument.
	  * If not enabled, the new line will be included in the quoted text. */
	val newLineBreakQuotes: Boolean
	/** Tab character can separate arguments line space char. If disabled, tabs will be treated
	  * as normal text characters. */
	val tabAsSeparator: Boolean
	/** Allow escape character at the most end of input text, it will be treated as normal
	  * characters due to there are nothing can be escaped. */
	val allowEscapeAtEnd: Boolean
	/** Even if the char after the escape char is not special, escape it either. This will make
	  * the escape char disappear where the following char will be output as usual. If disabled,
	  * The escape char will output as-is when the following is non-special character. */
	val escapeNonSpecialChars: Boolean
	
	private object checker {
		inline def isSeparator (c: Char): Boolean =
			if c == ' ' then true
			else if newLineAsSeparator && (c == '\n') then true
			else if tabAsSeparator && (c == '\t') then true
			else false
		
		inline def isQuote (c: Char): Boolean =
			(c == '\'') || (c == '"')
		
		inline def isEscape (c: Char): Boolean =
			c == '\\'
		
		inline def isEscapableInQuote (c: Char): Boolean =
			isQuote(c) || isEscape(c)
		
		inline def isEscapable (c: Char): Boolean =
			isEscapableInQuote(c) || isSeparator(c)
		
		inline def isForbidden (c: Char): Boolean =
			if !allowNewLine && ((c == '\n') || (c == '\r')) then true
			else false
		
		inline def isForbiddenInQuotes (c: Char): Boolean =
			if !allowNewLineInQuotes && ((c == '\n') || (c == '\r')) then true
			else false
		
		inline def isIgnore (c: Char): Boolean =
			if allowNewLine && (c == '\r') then true
			else false
		
		inline def isIgnoreInQuotes (c: Char): Boolean =
			if allowNewLineInQuotes && (c == '\r') then true
			else false
	}
	
	def parse (input: String, safeArgs: Int = 1): (args: Array[String], remainsRaw: String) = {
		given CanEqual[Char|Null, Null] = CanEqual.derived
		given CanEqual[Char|Null, Char] = CanEqual.derived
		
		import checker.*
		
		val parsed = ArrayBuffer[String]()
		val remains = StringBuilder()
		
		var i = 0
		var curr = StringBuilder()
		var inQuote: Char | Null = null
		var inEscaped: Char | Null = null
		
		inline def c = input(i)
		
		inline def hasNext = i + 1 < input.length
		
		inline def isInQuotes = inQuote != null
		
		inline def continue ()(using label: boundary.Label[Unit]): Unit = {
			i += 1; break()
		}
		
		while (i < input.length) {
			boundary {

				// now required counts of args are already parsed, put everything remains to remains
				if (parsed.length >= safeArgs) {
					remains += c
				}
				
				if (isInQuotes) {
					if (isForbiddenInQuotes(c))
						throw IllegalArgumentException(
							s"""Not allowed characters in a quoted text: ${c.toUnicodeString}
							   |  At :$i under:
							   |${input.indent(4)}""".stripMargin
						)
					if (isIgnoreInQuotes(c))
						continue()
				}
				else {
					if (isForbidden(c))
						throw IllegalArgumentException(
							s"""Not allowed characters in input: ${c.toUnicodeString}
							   |  At :$i under:
							   |${input.indent(4)}""".stripMargin
						)
					if (isIgnore(c))
						continue()
				}
				
				if (inEscaped != null) {
					if (if isInQuotes then isEscapableInQuote(c) else isEscapable(c))
						curr += c
					else if escapeNonSpecialChars then
						curr += c
					else
						curr += inEscaped.asInstanceOf[Char] += c
					inEscaped = null
					continue()
				}
				if (isEscape(c)) {
					inEscaped = '\\'
					continue()
				}
				
				if (isQuote(c)) {
					if !isInQuotes then
						inQuote = c
					else if inQuote != c then
						if allowQuoteInQuotes then
							curr += c
						else
							throw IllegalArgumentException(
								s"""Mismatched quote character in a quoted text: expected $inQuote but got $c
								   |  At :$i under:
								   |${input.indent(4)}""".stripMargin
							)
					else
						inQuote = null
					continue()
				}
				
				if (!isInQuotes && isSeparator(c)) {
					if (curr.nonEmpty)
						parsed += curr.toString
					curr = curr.empty
					continue()
				}
				
				curr += c
				continue()
				
			}
		}
		
		if (inEscaped != null) {
			if allowEscapeAtEnd then
				curr += inEscaped.asInstanceOf[Char]
			else throw IllegalArgumentException(
				s"""There are no next character to be escaped for an escape character.
				   |  At :$i under:
				   |${input.indent(4)}""".stripMargin
			)
		}
		
		if ((inQuote != null) && !allowUnclosedQuotes) {
			throw IllegalArgumentException(
				s"""Reached end of input while still in a quoted text started with $inQuote
				   |  At :$i under:
				   |${input.indent(4)}""".stripMargin
			)
		}
		
		if (curr.nonEmpty)
			parsed += curr.toString
		
		(parsed.toArray, remains.toString)
		
	}
	
}
