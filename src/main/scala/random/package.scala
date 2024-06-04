package cc.sukazyo.std

import random.RandomGenerator.CanGenerateDouble

package object random {
	
	class ChancePossibility[T <: Any] (val one: T)(using possibility: Double)(using generator: CanGenerateDouble) {
		def nor[U] (another: U): T | U =
			if generator.nextDouble < possibility then one else another
	}
	
	extension (num: Double) {
		
		def chance_is (one: Boolean)(using CanGenerateDouble): Boolean =
			ChancePossibility(one)(using num) nor !one
		
		def chance_is[T <: Any] (one: T)(using CanGenerateDouble): ChancePossibility[T] =
			ChancePossibility(one)(using num)
		
	}
	
	class IfInChanceResult [Return] (val result: Option[Return]) {
		def orElse [RetElse] (cbk: =>RetElse): Return|RetElse =
			result getOrElse cbk
	}
	
	def if_in_chance [Return] (possibility: Double)(cbk: =>Return)(using CanGenerateDouble): IfInChanceResult[Return] =
		if (possibility chance_is true) IfInChanceResult[Return](Some(cbk))
		else IfInChanceResult[Return](None)
	
}
