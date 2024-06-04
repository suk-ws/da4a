package cc.sukazyo.std

class TestMath extends BaseTestsSuite {
	
	import math.dsl._
	
	"for the predefined values" - {
		
		"a half in double" - {
			"should be 0.5" in { a_half shouldBe 0.5d }
			"should be the same with 1 over 2" in { a_half shouldBe (1 over 2) }
		}
		"a quarter in double" - {
			"should be 0.25" in { a_quarter shouldBe 0.25d }
			"should be the same with 1 over 4" in { a_quarter shouldBe (1 over 4) }
		}
		
	}
	
	"the `over` function (aka. `/-/` operator)" - {
		
		"should be able to use as an infix operator" in {
			assertCompiles("val a: Double = 1 over 2")
			assertCompiles("val a: Double = 1 /-/ 2")
		}
		
		"should works with all number types" in {
			val int: Int = 1
			val char: Char = 34
			val long: Long = 12389
			val short: Short = 12
			val byte: Byte = 12
			val double: Double = 875
			val float: Float = 0.5f
			var x: Double = 0
			assertCompiles("x = int /-/ char")
			assertCompiles("x = char over long")
			assertCompiles("x = long over byte")
			assertCompiles("x = float over int")
			assertCompiles("x = byte /-/ short")
			assertCompiles("x = double over int")
			assertCompiles("x = int /-/ float")
			assertCompiles("x = short /-/ short")
			assertCompiles("x = float over byte")
			assertCompiles("x = short /-/ char")
		}
		
		//noinspection ScalaUnusedExpression
		"should be the same with divide operator `/` in double" in {
			(23 over 128) shouldEqual (23.0 / 128.0)
			(12.7 over 9) shouldEqual (12.7 / 9.0)
			(80 /-/ 211.7) shouldEqual (80.0 / 211.7)
			(3f /-/ 0.2f) shouldEqual (3f.toDouble / 0.2f.toDouble)
		}
		
	}
	
	"the `pow` function (aka. `*^` operator)" - {
		
		"should be able to use as an infix operator" in {
			assertCompiles("val a = 1 *^ 2")
		}
		
		"should works with all types numbers" in {
			val int: Int = 1
			val char: Char = 34
			val long: Long = 12389
			val short: Short = 12
			val byte: Byte = 12
			val double: Double = 875
			val float: Float = 0.5f
			var x: Double = 0
			assertCompiles("x = int *^ char")
			assertCompiles("x = char *^ long")
			assertCompiles("x = long *^ byte")
			assertCompiles("x = float *^ int")
			assertCompiles("x = byte *^ short")
			assertCompiles("x = double *^ int")
			assertCompiles("x = int *^ float")
			assertCompiles("x = short *^ short")
			assertCompiles("x = float *^ byte")
			assertCompiles("x = short *^ char")
		}
		
		//noinspection ScalaUnusedExpression
		"should be the same with math.pow" in {
			import scala.math.pow
			(23 *^ 12) shouldEqual pow(23.0, 12.0)
			(12.7 *^ 9) shouldEqual pow(12.7d, 9d)
			(80 *^ 2.7) shouldEqual pow(80d, 2.7d)
			(3f *^ 0.2f) shouldEqual pow(3f.toDouble, 0.2f.toDouble)
		}
		
	}
	
	"the `asPercentage` function" - {
		
		"should be a suffix method, accepts all types of numbers, and returns number with the same type" in {
			var int: Int = 1
			var char: Char = 34
			var long: Long = 12389
			var short: Short = 12
			var byte: Byte = 12
			var double: Double = 875
			var float: Float = 0.5f
			assertCompiles("int = int.asPercentage")
			assertCompiles("char = char.asPercentage")
			assertCompiles("long = long.asPercentage")
			assertCompiles("short = short.asPercentage")
			assertCompiles("byte = byte.asPercentage")
			assertCompiles("double = double.asPercentage")
			assertCompiles("float = float.asPercentage")
		}
		
		//noinspection ScalaUnusedExpression
		"should be the number multiplies 100" in {
			23.asPercentage shouldEqual (23 * 100)
			12.7.asPercentage shouldEqual (12.7 * 100)
			80.asPercentage shouldEqual (80 * 100)
			3f.asPercentage shouldEqual (3f * 100)
		}
		
	}
	
}
