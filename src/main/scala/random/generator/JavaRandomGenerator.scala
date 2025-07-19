package cc.sukazyo.std
package random.generator

import random.generator.RandomGenerator.*

import java.util.Random as JRandom

class JavaRandomGenerator (val generator: JRandom)
	extends RandomGenerator
		with CanResetSeed [Long]
		with RandomBooleanGenerator
		with RandomByteGenerator
		with RandomByteArrayGenerator
		with RandomIntGenerator
		with RandomLongGenerator
		with RandomDoubleGenerator
		with RandomFloatGenerator {
	
	override def setSeed (seed: Long): Unit =
		generator.setSeed(seed)
	
	override def nextBoolean: Boolean =
		generator.nextBoolean
	
	override def nextByte: Byte = {
		val byteArray: Array[Byte] = Array.ofDim(1)
		generator.nextBytes(byteArray)
		byteArray(0)
	}
	
	override def fillByteArray (bytes: Array[Byte]): Unit =
		generator.nextBytes(bytes)
	
	override def bytes (length: Int): Array[Byte] =
		val byteArray: Array[Byte] = Array.ofDim(length)
		this.fillByteArray(byteArray)
		byteArray
	
	override def nextInt: Int =
		generator.nextInt
	
	override def nextLong: Long =
		generator.nextLong
	
	override def nextDouble: Double =
		generator.nextDouble
	
	override def nextFloat: Float =
		generator.nextFloat
	
	override def nextInt (end: Int): Int =
		generator.nextInt(end)
	
	override def nextInt (start: Int, end: Int): Int =
		// todo: fix this native failure
//		generator.nextInt(start, end + 1)
		if start > end then throw new IllegalArgumentException("Start integer must be greater than end integer")
		nextInt(end - start + 1) + start
	
}

object JavaRandomGenerator {
	
	implicit val defaults: JavaRandomGenerator = JavaRandomGenerator()
	
	def apply (): JavaRandomGenerator =
		new JavaRandomGenerator(new JRandom())
	def apply (seed: Long): JavaRandomGenerator =
		new JavaRandomGenerator(new JRandom(seed))
	def apply (generator: JRandom): JavaRandomGenerator =
		new JavaRandomGenerator(generator)
	
}
