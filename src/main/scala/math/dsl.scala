package cc.sukazyo.std
package math

import scala.annotation.targetName

object dsl {
	
	extension [Num] (numerator: Num)(using numOps: Numeric[Num]) {
		
		/** Operator that allows you to do `numerator / denominator`.
		  *
		  * Unlike normal `/` operator, this operator always converts two numbers to double and
		  * returns a double number.
		  *
		  * @example
		  * {{{
		  *     val a = 1 over 2
		  *     a: Double = 0.5
		  *     val b = 1 /-/ 4
		  *     b: Double = 0.25
		  * }}}
		  */
		infix def over [Denominator] (denominator: Denominator)(using denOps: Numeric[Denominator]): Double =
			numOps.toDouble(numerator) / denOps.toDouble(denominator)
		/** Operator that allows you to do `numerator / denominator`.
		  *
		  * Unlike normal `/` operator, this operator always converts two numbers to double and
		  * returns a double number.
		  *
		  * @example
		  * {{{
		  *     val a = 1 over 2
		  *     a: Double = 0.5
		  *     val b = 1 /-/ 4
		  *     b: Double = 0.25
		  * }}}
		  */
		infix def /-/ [Denominator] (denominator: Denominator)(using denOps: Numeric[Denominator]): Double =
			over(denominator)
		
		/** An operator that allows you to do `numerator ^ exponent`.
		  *
		  * This do exactly the same with `scala.math.pow(numerator.toDouble, exponent.toDouble)`.\
		  *
		  * @example
		  * {{{
		  *     val a = 2 *^ 3
		  *     a: Double = 8.0
		  * }}}
		  */
		@targetName("pow")
		infix def *^ [Exponent] (exponent: Exponent) (using expOps: Numeric[Exponent]): Double =
			scala.math.pow(numOps.toDouble(numerator), expOps.toDouble(exponent))
		
	}
	
	extension [Num] (numeric: Num) (using op: Numeric[Num]) {
		/** Converts this number to a number that should be shown as a percentage.
		  *
		  * Normally this is the same as `this / 100`, and notice that it does not automatically
		  * convert the result to a double or any other type, so take attention with yourself.
		  *
		  * @example
		  * {{{
		  *     // 0.23 is 23%
		  *     val a = 0.23
		  *     a: Double = 0.23
		  *     val a2 = a.asPercentage
		  *     a2: Double = 23
		  *     // 0.2585 is 25.85%
		  *     val b = 0.2585.asPercentage
		  *     b: Double = 25.85
		  *     // 10 is 1000%, and it will continue being an Int
		  *     val int = 10
		  *     int: Int = 10
		  *     val int2 = int.asPercentage
		  *     int2: Int = 1000
		  *     // due to Byte has only 128 capabilities, so it will overflow to an unexpected
		  *     // result
		  *     val byte_DANGER: Byte = 30
		  *     byte_DANGER: Byte = 30
		  *     val byte2 = byte_DANGER.asPercentage
		  *     byte2: Byte = -72
		  * }}}
		  */
		def asPercentage: Num =
			import op.mkNumericOps
			numeric * op.fromInt(100)
	}
	
	/** Alias for 1/2 */
	val a_half: Double = 1 over 2
	/** Alias for 1/4 */
	val a_quarter: Double = 1 over 4
	
}
