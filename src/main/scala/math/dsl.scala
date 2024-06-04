package cc.sukazyo.std
package math

import scala.annotation.targetName

object dsl {
	
	extension [Numerator] (numerator: Numerator)(using numOps: Numeric[Numerator]) {
		
		def over [Denominator] (denominator: Denominator)(using denOps: Numeric[Denominator]): Double =
			numOps.toDouble(numerator) / denOps.toDouble(denominator)
		def /-/ [Denominator] (denominator: Denominator)(using denOps: Numeric[Denominator]): Double =
			over(denominator)
		
		@targetName("pow")
		def *^ [Exponent] (exponent: Exponent) (using expOps: Numeric[Exponent]): Double =
			scala.math.pow(numOps.toDouble(numerator), expOps.toDouble(exponent))
		
	}
	
	extension [Num] (numeric: Num) (using op: Numeric[Num]) {
		def asPercentage: Num =
			import op.mkNumericOps
			numeric * op.fromInt(100)
	}
	
	val a_half: Double = 1 over 2
	val a_quarter: Double = 1 over 4
	
}
