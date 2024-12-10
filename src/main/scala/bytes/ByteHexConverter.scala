package cc.sukazyo.std
package bytes

/** Added the [[toHex]] method to [[Byte]] and [[Array]]`[`[[Byte]]`]`.
  *
  * the [[toHex]] method will takes [[Byte]] as a binary byte and convert
  * it to the hex [[String]] that can describe the binary byte. there are
  * always 2 digits unsigned hex number.
  *
  * for example, byte `0` is binary `0000 0000`, it will be converted to
  * `"00"`, and the byte `-1` is binary `1111 1111` which corresponding
  * `"ff"`.
  *
  * while converting byte array, the order is: the 1st element of the array
  * will be put most forward, then the following added to the tail of hex string.
  *
  * @example {{{
  *     scala> 0.toByte.toHex
  *     val res6: String = 00
  *
  *     scala> 15.toByte.toHex
  *     val res10: String = 0f
  *
  *     scala> -1.toByte.toHex
  *     val res7: String = ff
  *
  *     scala> Array[Byte](0, 1, 2, 3).toHex
  *     val res5: String = 00010203
  * }}}
  *
  */
object ByteHexConverter {
	
	/**
	  * @note a part of [[ByteHexConverter]] extension
	  */
	implicit class Byte2Hex (b: Byte) {
		
		/** Convert the binary representation of the `Byte` to a hexadecimal string.
		  *
		  * The byte is converted to a two-digit hexadecimal string.
		  * The resulting string represents the hexadecimal value of the byte.
		  *
		  * @example {{{
		  *     val byte: Byte = 0
		  *     val hexString = byte.toHex
		  *     // hexString: String = "00"
		  *
		  *     val byte: Byte = -1
		  *     val hexString = byte.toHex
		  *     // hexString: String = "ff"
		  * }}}
		  *
		  * @return A string representing the hexadecimal value of the byte.
		  *
		  * @see This method is a part of [[ByteHexConverter]] extension
		  * @since 0.2.0
		  */
		def toHex: String = (b >> 4 & 0xf).toHexString + (b & 0xf).toHexString
		
	}
	
	/**
	  * @note a part of [[ByteHexConverter]] extension
	  */
	implicit class ByteArray2Hex (data: Array[Byte]) {
		
		/** Convert the binary representation of the `Array[Byte]` to a hexadecimal string.
		  *
		  * Each byte in the array is converted to a two-digit hexadecimal string.
		  * The resulting string is a concatenation of these hexadecimal representations
		  * in the same order as the bytes in the array.
		  * 
		  * This method equals to the following code:
		  * {{{
		  *     byteArray.map(_.toHex).mkString
		  * }}}
		  * 
		  * @example {{{
		  *     val byteArray = Array[Byte](0, 1, -1, 3)
		  *     val hexString = byteArray.toHex
		  *     // hexString: String = "0001ff03"
		  * }}}
		  *
		  * @return A string representing the hexadecimal values of the bytes in the array.
		  * 
		  * @see This method is a part of [[ByteHexConverter]] extension
		  * @since 0.2.0
		  */
		def toHex: String =
			val sb = StringBuilder()
			for (b <- data) sb ++= (b toHex)
			sb toString
		
	}
	
}
