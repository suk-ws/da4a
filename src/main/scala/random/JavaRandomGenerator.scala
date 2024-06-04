package cc.sukazyo.std
package random

import random.RandomGenerator.CanGenerateDouble

import java.util.Random as JRandom

class JavaRandomGenerator (val generator: JRandom)
extends RandomGenerator
with CanGenerateDouble {
	
	override def nextDouble: Double =
		generator.nextDouble
	
}

object JavaRandomGenerator {
	
	object defaults:
		given defaultJavaRandomGenerator: JavaRandomGenerator = JavaRandomGenerator()
	
	def apply (): JavaRandomGenerator =
		new JavaRandomGenerator(new JRandom())
	def apply (seed: Long): JavaRandomGenerator =
		new JavaRandomGenerator(new JRandom(seed))
	def apply (generator: JRandom): JavaRandomGenerator =
		new JavaRandomGenerator(generator)
	
//	def apply (seed: Long, factory: RandomGeneratorFactory): JavaRandomGenerator = {
//		new JavaRandomGenerator(factory.createRandom(seed))
//	}
//
//	def apply (factory: RandomGeneratorFactory): JavaRandomGenerator = {
//		new JavaRandomGenerator(factory.createRandom())
//	}

}
