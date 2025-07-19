package cc.sukazyo.std
package random.generator

trait RandomGenerator

object RandomGenerator {
	
	trait HasSeed [T] extends RandomGenerator:
		def seed: T
	
	trait CanResetSeed [T] extends RandomGenerator:
		def setSeed (seed: T): Unit
	
	trait RandomDoubleGenerator extends RandomGenerator:
		def nextDouble: Double
	
	trait RandomFloatGenerator extends RandomGenerator:
		def nextFloat: Float
	
	trait RandomIntGenerator extends RandomGenerator:
		def nextInt: Int
		/** random from 0 (included) to end (not included) */
		def nextInt (end: Int): Int
		/** random from start to end (all included) */
		def nextInt(start: Int, end: Int): Int
	
	trait RandomLongGenerator extends RandomGenerator:
		def nextLong: Long
	
	trait RandomBooleanGenerator extends RandomGenerator:
		def nextBoolean: Boolean
	
	trait RandomByteGenerator extends RandomGenerator:
		def nextByte: Byte
	
	trait RandomByteArrayGenerator extends RandomGenerator:
		def fillByteArray (bytes: Array[Byte]): Unit
		def bytes (length: Int): Array[Byte]
	
}
