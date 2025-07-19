package cc.sukazyo.std

import random.generator.ScalaStaticRandomGenerator
import random.generator.RandomGenerator.{RandomDoubleGenerator, RandomIntGenerator}

import scala.collection.mutable.ListBuffer

package object random {
	
	implicit val defaults: ScalaStaticRandomGenerator.defaults.type = ScalaStaticRandomGenerator.defaults
	
	class ChancePossibility[T <: Any] (val one: T)(using possibility: Double)(using generator: RandomDoubleGenerator) {
		infix def nor[U] (another: U): T | U =
			if generator.nextDouble < possibility then one else another
	}
	
	implicit def conv_ChancePossibilityBoolean_Boolean
	(in: ChancePossibility[Boolean]): Boolean = in nor !in.one
	
	extension (num: Double) {
		
		infix def chance_is[T <: Any] (one: T)(using RandomDoubleGenerator): ChancePossibility[T] =
			ChancePossibility(one)(using num)
		
	}
	
	class IfInChanceResult [Return] (val result: Option[Return]) {
		infix def orElse [RetElse] (cbk: =>RetElse): Return|RetElse =
			result getOrElse cbk
	}
	
	def if_in_chance [Return] (possibility: Double)(cbk: =>Return)(using RandomDoubleGenerator): IfInChanceResult[Return] =
		if (possibility chance_is true) IfInChanceResult[Return](Some(cbk))
		else IfInChanceResult[Return](None)
	
	class ArrayChooser [T] (array: Seq[T]) {
		
		def chooseOne (using generator: RandomIntGenerator): T = {
			if (array.isEmpty) throw IllegalArgumentException("The array that want to choose from must not empty!")
			array(generator.nextInt(array.length))
		}
		
		def choose (n: Int)(using generator: RandomIntGenerator): List[T] = {
			
			if (array.length < n) throw IllegalArgumentException("Array does not contains enough number of items to choose!")
			
			val indexes = Array.fill(array.length)(false)
			var chosen = 0
			while (chosen < n) {
				val chosenIndex = generator.nextInt(array.length)
				if (!indexes(chosenIndex))
					indexes(chosenIndex) = true
					chosen += 1
			}
			
			val result = ListBuffer.empty[T]
			for (i <- indexes.indices) {
				if (indexes(i)) result += array(i)
			}
			
			result.toList
			
		}
		
	}
	
}
