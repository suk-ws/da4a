package cc.sukazyo.std
package text

object CharStringify {
	
	implicit class CharStringifyOps (c: Char) {
		
		def toUnicodeString: String = {
			if (!c.isControl) {
				String.format("\\u%04x", c.toInt)
			} else {
				s"'$c'"
			}
		}
		
	}
	
}
