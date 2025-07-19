package cc.sukazyo.std
package random.generator

import random.generator.RandomGenerator.*

import scala.util.Random

class ScalaStaticRandomGenerator
	extends RandomGenerator
		with RandomDoubleGenerator
		with RandomIntGenerator {
	
	override def nextDouble: Double =
		Random.nextDouble
	
	override def nextInt: Int =
		Random.nextInt
	
	override def nextInt (end: Int): Int =
		Random.nextInt(end)
	
	override def nextInt (start: Int, end: Int): Int = {
		if start > end then throw new IllegalArgumentException("Start integer must be greater than end integer")
		Random.nextInt(end - start + 1) + start
	}
	
}

object ScalaStaticRandomGenerator {
	implicit val defaults: ScalaStaticRandomGenerator = new ScalaStaticRandomGenerator
}
